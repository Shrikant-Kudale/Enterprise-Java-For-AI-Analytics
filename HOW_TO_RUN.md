# How to run the backdated commit pipeline (VS Code, local)

The ZIP holds 92 day folders (Mar 1 - May 31, 2026), `README.md`, `metadata.json` and `pipeline.py`.
`pipeline.py` creates a **separate** git repo, makes backdated commits for every day and pushes to GitHub.

## One-time setup
1. Install Python 3.8+ and Git. In VS Code you should already be signed in to GitHub (Git Credential Manager / `gh auth login`).
2. On github.com create a new **empty** repository named `enterprise-java-for-ai-analytics` (no README, no .gitignore). Copy its HTTPS URL.
3. Make sure your commit e-mail is verified on GitHub (Settings > Emails), otherwise commits will not show on your contribution graph:
   `git config --global user.email "you@example.com"` and `git config --global user.name "Your Name"`.

## Run
1. Unzip `enterprise-java-for-ai-analytics.zip`.
2. VS Code: File > Open Folder... > the unzipped `enterprise-java-for-ai-analytics` folder.
3. Open the terminal (Ctrl+`) and run `python pipeline.py` (use `python3` on macOS/Linux).
4. Answer the prompts:
   - **Maximum commits per day** - type e.g. `8`. Each day then gets a random 5-8 commits (max-3 ... max) at random times (08:00-23:45).
   - **Folder for the new git repo** - press Enter for the default (a sibling folder next to the unzipped one).
   - **Push mode** - `end` (default, one push at the end), `monthly`, `daily` or `none`.
   - **GitHub repo URL** - paste the URL from step 2, then confirm with `y`.
5. Wait about 2-5 minutes (hundreds of commits). Refresh the GitHub repo: commits appear dated 2026-03-01 ... 2026-05-31 and the contribution graph fills for those days.

## Options
- Preview only: `python pipeline.py --dry-run`
- Non-interactive: `python pipeline.py --max 8 --repo ../my-repo --remote https://github.com/USER/enterprise-java-for-ai-analytics.git --push end --yes`
- Re-run a slice: `--from-day 40 --to-day 92`
- Different random pattern: `--seed 7`

## Notes
- Every commit is dated on the day it belongs to; all 92 days get at least one commit.
- If the push fails (auth/remote), commits stay local; fix the remote and run `git push -u origin main` inside the repo folder.
- GitHub shows backdated commits with their set dates; be upfront about that if asked, and keep your own real commits going after May 31.
- Run any lab: `cd day-01-jvm-memory-and-jit && javac JvmMemoryAndJit.java && java JvmMemoryAndJit` (JDK 21+).
