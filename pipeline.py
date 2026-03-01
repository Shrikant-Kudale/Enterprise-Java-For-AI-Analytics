#!/usr/bin/env python3
"""
Backdated commit pipeline for `enterprise-java-for-ai-analytics`  (March 1 -> May 31, 2026 = 92 days).

It reads the day folders that sit next to this script, builds a SEPARATE git repository and creates
backdated commits for EVERY day. You choose the MAXIMUM commits per day (e.g. 8); each day then gets a
random count between (max-3) and max (e.g. 5,6,7,8) at random times of the day. Then it pushes to GitHub.

Run:   python pipeline.py            (interactive)
       python pipeline.py --dry-run  (print the plan only, touches nothing)
Needs: Python 3.8+, git, and a GitHub login already configured (VS Code / Git Credential Manager / gh).
"""
import argparse
import datetime as dt
import json
import random
import re
import subprocess
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
START, END = dt.date(2026, 3, 1), dt.date(2026, 5, 31)
BASE_STAGES = [("concepts", "docs"), ("memory architecture", "docs"), ("interview edge cases", "docs"), ("Q&A", "docs"),
               ("run instructions and checklist", "docs"), ("class scaffold", "chore"), ("lab implementation + benchmark", "feat"),
               ("practice checklist and progress", "docs")]
JOURNAL = ["reviewed notes", "re-ran benchmark and compared results", "refactored comments", "revised weak Q&A answers",
           "added follow-up questions", "cleaned up formatting"]


def ask(prompt, default=None, cast=str):
    while True:
        raw = input(f"{prompt}" + (f" [{default}]" if default is not None else "") + ": ").strip()
        if not raw and default is not None:
            return default
        try:
            return cast(raw)
        except ValueError:
            print("  invalid value, try again")


def git(repo, *args, env=None, check=True):
    r = subprocess.run(["git", "-c", "commit.gpgsign=false", *args], cwd=repo, env=env, text=True, capture_output=True)
    if check and r.returncode != 0:
        sys.exit(f"git {' '.join(args)} failed:\n{r.stdout}\n{r.stderr}")
    return r


def split_notes(text):
    cuts = [0]
    for n in (2, 3, 4, 5):
        m = re.search(rf"\n## {n}\.", text)
        cuts.append(m.start() + 1 if m else len(text))
    cuts.append(len(text))
    return [text[cuts[i]:cuts[i + 1]] for i in range(5)]


def load_days():
    meta = json.loads((HERE / "metadata.json").read_text(encoding="utf-8"))
    days = []
    for folder, info in meta.items():
        d = HERE / folder
        java = next(d.glob("*.java"))
        notes = (d / "notes.md").read_text(encoding="utf-8")
        title = re.match(r"# Day \d+ - (.*)", notes).group(1).strip()
        days.append(dict(n=info["day"], folder=folder, date=dt.date.fromisoformat(info["backdate"]), title=title,
                         cls=java.stem, java=java.read_text(encoding="utf-8"), notes=split_notes(notes)))
    return sorted(days, key=lambda x: x["n"])


def plan_day(day, max_per_day, rng):
    lo = max(1, max_per_day - 3)
    n = rng.randint(lo, max_per_day)
    stages = list(range(max(len(BASE_STAGES), n)))
    groups = [stages[i * len(stages) // n:(i + 1) * len(stages) // n] for i in range(n)]
    minutes = sorted(rng.sample(range(8 * 60, 23 * 60 + 45), n))
    return groups, [(m // 60, m % 60, rng.randint(0, 59)) for m in minutes]


def label(i):
    return BASE_STAGES[i] if i < len(BASE_STAGES) else (JOURNAL[(i - len(BASE_STAGES)) % len(JOURNAL)], "docs")


def apply_stage(repo, day, i, progress_rows):
    folder = repo / day["folder"]
    folder.mkdir(exist_ok=True)
    notes = folder / "notes.md"
    if i <= 4:   # cumulative notes sections 1..5
        notes.write_text("".join(day["notes"][:i + 1]), encoding="utf-8", newline="\n")
    elif i == 5:
        (folder / f"{day['cls']}.java").write_text(
            f"/** Day {day['n']:02d} - {day['title']} (scaffold). */\npublic class {day['cls']} {{\n"
            f"    public static void main(String[] args) {{ System.out.println(\"Day {day['n']:02d}: work in progress\"); }}\n}}\n",
            encoding="utf-8", newline="\n")
    elif i == 6:
        (folder / f"{day['cls']}.java").write_text(day["java"], encoding="utf-8", newline="\n")
    elif i == 7:
        (folder / "practice.md").write_text(
            f"# Practice - Day {day['n']:02d}: {day['title']}\n\n- [ ] Explain the concept aloud in 2 minutes.\n"
            f"- [ ] Change one value in `{day['cls']}.java`, predict the result, then run it.\n- [ ] Answer every Q&A item without notes.\n",
            encoding="utf-8", newline="\n")
        progress_rows.append(f"| {day['n']:02d} | {day['date']} | {day['title']} | done |")
        (repo / "PROGRESS.md").write_text("# Progress\n\n| Day | Date | Topic | Status |\n|----:|------|-------|--------|\n" + "\n".join(progress_rows) + "\n",
                                          encoding="utf-8", newline="\n")
    else:        # extra study-log commits (only when max commits/day > 8)
        log = folder / "journal.md"
        old = log.read_text(encoding="utf-8") if log.exists() else f"# Study log - Day {day['n']:02d}\n\n"
        log.write_text(old + f"- {label(i)[0]}\n", encoding="utf-8", newline="\n")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo"); ap.add_argument("--remote"); ap.add_argument("--max", type=int)
    ap.add_argument("--push", choices=["end", "monthly", "daily", "none"]); ap.add_argument("--seed", type=int, default=2026)
    ap.add_argument("--from-day", type=int, default=1); ap.add_argument("--to-day", type=int, default=92)
    ap.add_argument("--dry-run", action="store_true"); ap.add_argument("--yes", action="store_true")
    a = ap.parse_args()

    days = [d for d in load_days() if a.from_day <= d["n"] <= a.to_day]
    print(f"Loaded {len(days)} day folders ({days[0]['date']} -> {days[-1]['date']}).")
    max_per_day = a.max or ask("Maximum commits per day (each day gets a random 'max-3'..'max', e.g. 8 -> 5-8)", 8, int)
    max_per_day = max(1, min(max_per_day, 14))
    default_repo = str(HERE.parent / "enterprise-java-for-ai-analytics-repo")
    repo = Path(a.repo or (default_repo if a.dry_run else ask("Folder for the new git repo", default_repo))).expanduser().resolve()

    rng_plan = random.Random(a.seed)
    plans = {d["n"]: plan_day(d, max_per_day, rng_plan) for d in days}
    total = sum(len(g) for g, _ in plans.values())
    print(f"Plan: {total} commits over {len(days)} days (avg {total / len(days):.1f}/day), random times 08:00-23:45.")
    if a.dry_run:
        for d in days[:5]:
            g, t = plans[d["n"]]
            print(f"  {d['date']}: {len(g)} commits at " + ", ".join(f"{h:02d}:{m:02d}" for h, m, _ in t))
        print("  ... (dry run, nothing written)")
        return

    remote = a.remote
    push_mode = a.push or ask("Push mode: end / monthly / daily / none", "end")
    if not (repo / ".git").exists():
        repo.mkdir(parents=True, exist_ok=True)
        git(repo, "init"); git(repo, "checkout", "-B", "main")
    has_commits = git(repo, "rev-parse", "HEAD", check=False).returncode == 0
    if has_commits and not a.yes and ask("Repo already has commits. Add on top of them? (y/n)", "n").lower() != "y":
        sys.exit("Aborted.")
    if git(repo, "remote", "get-url", "origin", check=False).returncode != 0 and push_mode != "none":
        remote = remote or ask("GitHub repo URL (empty repo, e.g. https://github.com/USER/enterprise-java-for-ai-analytics.git)", "")
        if remote:
            git(repo, "remote", "add", "origin", remote)
        else:
            push_mode = "none"
    email = git(repo, "config", "user.email", check=False).stdout.strip()
    name = git(repo, "config", "user.name", check=False).stdout.strip()
    if not email or not name:
        name = name or ask("git user.name"); email = email or ask("git user.email (must match your GitHub email)")
        git(repo, "config", "user.name", name); git(repo, "config", "user.email", email)
    print(f"Committing as {name} <{email}>  (this email must be verified on your GitHub account to show on the graph).")
    if not a.yes and ask(f"Create {total} backdated commits now? (y/n)", "y").lower() != "y":
        sys.exit("Aborted.")

    tz = dt.datetime.now().astimezone().tzinfo
    progress_rows = []
    made = 0
    for d in days:
        groups, times = plans[d["n"]]
        if d["n"] == days[0]["n"] and not has_commits:   # root files ride along with the very first commit
            for fn in ("README.md", "metadata.json"):
                (repo / fn).write_text((HERE / fn).read_text(encoding="utf-8"), encoding="utf-8", newline="\n")
            (repo / ".gitignore").write_text("*.class\nout/\n.idea/\n.vscode/\n", encoding="utf-8", newline="\n")
        for g, (h, m, s) in zip(groups, times):
            for i in g:
                apply_stage(repo, d, i, progress_rows)
            kinds = [label(i) for i in g]
            ctype = "feat" if any(k[1] == "feat" for k in kinds) else kinds[0][1]
            msg = f"{ctype}(day-{d['n']:02d}): {d['title']} - " + " + ".join(k[0] for k in kinds)
            stamp = dt.datetime(d["date"].year, d["date"].month, d["date"].day, h, m, s, tzinfo=tz).isoformat()
            env = {**__import__("os").environ, "GIT_AUTHOR_DATE": stamp, "GIT_COMMITTER_DATE": stamp}
            git(repo, "add", "-A")
            if git(repo, "commit", "-m", msg, env=env, check=False).returncode != 0:
                git(repo, "commit", "--allow-empty", "-m", msg, env=env)
            made += 1
        print(f"  {d['date']}  day {d['n']:02d}  {len(groups)} commits   ({made}/{total})")
        last_of_month = (d["date"] + dt.timedelta(days=1)).month != d["date"].month
        if push_mode == "daily" or (push_mode == "monthly" and last_of_month):
            r = git(repo, "push", "-u", "origin", "main", check=False)
            print("  pushed" if r.returncode == 0 else f"  push failed: {r.stderr.strip()}")
    if push_mode == "end":
        r = git(repo, "push", "-u", "origin", "main", check=False)
        print("Pushed to origin/main." if r.returncode == 0 else f"Push failed (commits are safe locally):\n{r.stderr}\nFix auth/remote, then run: git -C \"{repo}\" push -u origin main")
    print(f"Done: {made} commits in {repo}")


if __name__ == "__main__":
    main()
