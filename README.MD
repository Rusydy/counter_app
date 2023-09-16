# Counter App

> ## Description

This is a simple app what shows a webview of some short of games with hidden quests.

> ## Contributing

### Create branch from `main` branch

```bash
[main] $ git pull origin main
[main] $ git checkout -b <branch-name>
[branch-name] $ // do your work
```

### Push, Pull Request, Merge

always rebase before or after PR if any changes have been made to the main branch.

- Fetch all tags

    ```bash
    [branch-name] $ git fetch --all --tags
    ```

- Rebase

    ```bash
    [branch-name] $ git rebase -i --autosquash origin/main
    ```

- Push

    ```bash
    [branch-name] $ git push origin <branch-name>
    ```

### Create Pull Request

1. Always fetch all tags before creating a PR
2. Rebase the feature branch with the main branch
3. Create a descriptive commit message

    example:

    ```markdown
    [branch-name]: <commit-message> describe what you have done in this commit briefly

    <commit-description> describe what you have done in this commit in detail, including:
        <pre-commit-description> describe the background problem of this commit
        <commit-changes> describe what changes you have made in this commit
        <post-commit-description> describe the impact of this commit
    ```

4. If there are multiple commits, you should squash them into one commit if it is not necessary to have multiple commits, or group them into only several commits based on the changes made. So that the commit history is not too long and messy.

    > Note: Please avoid a meaningless commit message like `fixing bug`, `fixing typo`, `fixing code`, etc.

    The reason is that it will be hard to track what changes have been made in the commit history. everybody knows that you are fixing something, but...
    - what are you fixing?
    - what is the impact of the changes?
    - what is the background problem?
    - is the commit meaningful? etc.

5. Create a PR with the following format

    ```markdown
    [branch-name]: <PR-message> describe what you have done in this PR briefly

    <PR-description> describe what you have done in this PR in detail, including:
        <pre-PR-description> describe the background problem of this PR
        <PR-changes> describe what changes you have made in this PR
        <post-PR-description> describe the impact of this PR
    ```

### If there are any requests for changes

```bash
[branch-name] $ git add .
[branch-name] $ git commit --amend --no-edit
[branch-name] $ git push -f 
```
