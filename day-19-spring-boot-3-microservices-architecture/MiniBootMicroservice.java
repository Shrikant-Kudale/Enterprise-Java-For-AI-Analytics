import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;

/** Day 19 - tiny DI container + annotation routing + HTTP server (what Spring Boot does, minus the framework). Run: javac MiniBootMicroservice.java && java MiniBootMicroservice */
public class MiniBootMicroservice {
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) @interface Component { }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) @interface Get { String value(); }
    record Response(int status, String body) { }

    // ---------------------------------------------------------------- IoC container
    static final class Container {
        private final List<Class<?>> defs = new ArrayList<>();
        private final Map<Class<?>, Object> beans = new LinkedHashMap<>();
        private final Deque<Class<?>> creating = new ArrayDeque<>();
        Container register(Class<?>... cs) { defs.addAll(Arrays.asList(cs)); return this; }
        @SuppressWarnings("unchecked")
        <T> T get(Class<T> type) throws Exception {
            for (Object b : beans.values()) if (type.isInstance(b)) return (T) b;
            Class<?> impl = defs.stream().filter(type::isAssignableFrom).findFirst().orElseThrow(() -> new NoSuchElementException("no bean for " + type.getSimpleName()));
            if (creating.contains(impl)) throw new IllegalStateException("circular dependency: " + creating + " -> " + impl.getSimpleName());
            creating.push(impl);
            Constructor<?> ctor = Arrays.stream(impl.getDeclaredConstructors()).max(Comparator.comparingInt(Constructor::getParameterCount)).orElseThrow();
            ctor.setAccessible(true);
            Object[] args = new Object[ctor.getParameterCount()];
            for (int i = 0; i < args.length; i++) args[i] = get(ctor.getParameterTypes()[i]);
            Object bean = ctor.newInstance(args);
            creating.pop();
            beans.put(impl, bean);
            return (T) bean;
        }
        Collection<Object> all() { return beans.values(); }
    }

    // ---------------------------------------------------------------- application beans
    interface ModelRepository { Optional<double[]> weights(String id); }
    @Component static final class InMemoryModelRepository implements ModelRepository {
        public Optional<double[]> weights(String id) { return id.equals("m1") ? Optional.of(new double[]{0.5, -0.25}) : Optional.empty(); }
    }
    @Component static final class PredictionService {
        private final ModelRepository repo;
        PredictionService(ModelRepository repo) { this.repo = repo; }
        double predict(String model, double[] x) {
            double[] w = repo.weights(model).orElseThrow(() -> new NoSuchElementException("unknown model " + model));
            if (w.length != x.length) throw new IllegalArgumentException("expected " + w.length + " features");
            double s = 0; for (int i = 0; i < w.length; i++) s += w[i] * x[i];
            return 1 / (1 + Math.exp(-s));
        }
    }
    @Component static final class PredictionController {
        private final PredictionService svc;
        PredictionController(PredictionService svc) { this.svc = svc; }
        @Get("/health") Response health(Map<String, String> q) { return new Response(200, "{\"status\":\"UP\"}"); }
        @Get("/predict") Response predict(Map<String, String> q) {
            double[] x = Arrays.stream(q.getOrDefault("x", "").split(",")).filter(s -> !s.isBlank()).mapToDouble(Double::parseDouble).toArray();
            return new Response(200, "{\"model\":\"" + q.get("model") + "\",\"score\":" + svc.predict(q.get("model"), x) + "}");
        }
    }
    static final class A { A(B b) { } }
    static final class B { B(A a) { } }

    // ---------------------------------------------------------------- HTTP server wiring
    static final LongAdder requests = new LongAdder();
    static HttpServer start(Container c) throws Exception {
        Map<String, Object[]> routes = new HashMap<>();
        for (Object bean : c.all())
            for (Method m : bean.getClass().getDeclaredMethods())
                if (m.isAnnotationPresent(Get.class)) { m.setAccessible(true); routes.put(m.getAnnotation(Get.class).value(), new Object[]{bean, m}); }
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/", ex -> {
            requests.increment();
            Response r;
            try {
                String path = ex.getRequestURI().getPath();
                if (path.equals("/metrics")) r = new Response(200, "http_requests_total " + requests.sum() + "\n");
                else {
                    Object[] route = routes.get(path);
                    if (route == null) r = new Response(404, "{\"error\":\"not found\"}");
                    else r = (Response) ((Method) route[1]).invoke(route[0], query(ex.getRequestURI().getRawQuery()));
                }
            } catch (InvocationTargetException e) {
                Throwable t = e.getCause();
                r = t instanceof NoSuchElementException ? new Response(404, "{\"error\":\"" + t.getMessage() + "\"}")
                        : t instanceof IllegalArgumentException ? new Response(400, "{\"error\":\"" + t.getMessage() + "\"}")
                        : new Response(500, "{\"error\":\"internal\"}");
            } catch (Exception e) { r = new Response(500, "{\"error\":\"internal\"}"); }
            byte[] body = r.body().getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", r.body().startsWith("{") ? "application/json" : "text/plain");
            ex.sendResponseHeaders(r.status(), body.length);
            ex.getResponseBody().write(body);
            ex.close();
        });
        server.start();
        return server;
    }
    static Map<String, String> query(String raw) {
        Map<String, String> q = new HashMap<>();
        if (raw == null) return q;
        for (String kv : raw.split("&")) { String[] p = kv.split("=", 2); q.put(URLDecoder.decode(p[0], StandardCharsets.UTF_8), p.length > 1 ? URLDecoder.decode(p[1], StandardCharsets.UTF_8) : ""); }
        return q;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        Container c = new Container().register(InMemoryModelRepository.class, PredictionService.class, PredictionController.class);
        c.get(PredictionController.class);
        check(c.get(PredictionService.class) == c.get(PredictionService.class), "beans are singletons");
        try { new Container().register(A.class, B.class).get(A.class); check(false, "cycle"); } catch (IllegalStateException expected) { }

        HttpServer server = start(c);
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        HttpClient http = HttpClient.newBuilder().proxy(ProxySelector.of(null)).build();
        java.util.function.Function<String, HttpResponse<String>> get = p -> {
            try { return http.send(HttpRequest.newBuilder(URI.create(base + p)).build(), HttpResponse.BodyHandlers.ofString()); }
            catch (IOException | InterruptedException e) { throw new RuntimeException(e); }
        };
        check(get.apply("/health").statusCode() == 200 && get.apply("/health").body().contains("UP"), "health");
        HttpResponse<String> ok = get.apply("/predict?model=m1&x=2,2");
        Matcher m = java.util.regex.Pattern.compile("\"score\":([0-9.eE-]+)").matcher(ok.body());
        check(ok.statusCode() == 200 && m.find() && Math.abs(Double.parseDouble(m.group(1)) - 1 / (1 + Math.exp(-0.5))) < 1e-12, ok.body());
        check(get.apply("/predict?model=nope&x=1,2").statusCode() == 404, "unknown model -> 404");
        check(get.apply("/predict?model=m1&x=1").statusCode() == 400, "bad features -> 400");
        check(get.apply("/nothing").statusCode() == 404, "unknown route");
        check(get.apply("/metrics").body().contains("http_requests_total"), "metrics");

        int n = 3_000;
        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) get.apply("/predict?model=m1&x=1,2");
        double sec = (System.nanoTime() - t0) / 1e9;
        System.out.printf("sequential keep-alive requests: %.0f req/s (%.2f ms avg)%n", n / sec, sec * 1000 / n);
        server.stop(0);
        System.out.println("Day 19 OK");
    }
}
