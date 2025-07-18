# Contributing to Kanban Task Management App
 
Welcome, and thank you for considering contributing to this project! To maintain a clean and efficient development workflow, we follow a structured branching strategy and adhere to DevOps best practices.

## 🧭 Branching Strategy

We use **three main branches** to manage the lifecycle of code in this repository:

* `main`: The **production** branch. Always reflects stable, deployable code.
* `staging`: The **pre-production** branch. Used for final integration testing before merging to `main`.
* `dev`: The **active development** branch. All new features and fixes are first merged here.

### Workflow Summary

1.  Clone the repository (dev branch) and change directory into repository
    ```bash
    git clone -b dev https://github.com/AmaliTech-Training-Academy/kanban-task-management-backend.git
    cd kanban-task-management-backend
    ```
2.  Ensure you are on the dev branch

     ```bash
     git branch -v
     ```
  - If not on `dev` branch;
    
      ```bash
      git checkout -b dev
      git pull origin dev
      ```

3.  Create a new branch from `dev` for your feature or bugfix:

     ```bash
     git checkout -b feature/your-feature-name
     ```

4. Make your changes and commit with clear, descriptive messages.

5. Push your branch:

   ```bash
   git push origin feature/your-feature-name
   ```

6. Open a Pull Request (PR) **into `dev`**, not `main` or `staging`.

---

## ✅ Code Contribution Checklist

Before submitting a pull request:

* [ ] Ensure the branch is up-to-date with `dev`.
* [ ] Your changes pass all tests and build checks.
* [ ] Follow the code formatting/style guidelines used in the project.
* [ ] Add unit or integration tests where necessary.
* [ ] Document any new functionality or changes (code comments or README updates).
* [ ] Use semantic commit messages (see below).

---

## 📝 Commit Message Guidelines

Follow [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>(scope): short description
```

**Examples:**

* `feat(auth): add JWT-based login`
* `fix(ui): correct broken navbar layout`
* `chore: update dependencies`

**Common types:**

* `feat`: New feature
* `fix`: Bug fix
* `docs`: Documentation change
* `style`: Formatting (non-code logic changes)
* `refactor`: Code refactoring
* `test`: Adding or fixing tests
* `chore`: Maintenance or tooling changes

---

## 🧪 Testing and Validation

* Run all tests locally before pushing.
* Ensure secrets or sensitive data are not hardcoded or pushed.

---

## 🔐 Security and Access

* Do **not** push directly to `main` or `staging`.
* Use GitHub PR reviews to ensure peer-review and automated checks.
* Use `.env.example` files for environment variables instead of sharing secrets.

---

## 📦 CI/CD

All PRs trigger automated checks through our CI/CD pipeline. Make sure:

* Your branch passes linting, build, and test jobs.
* You do not break the pipeline configuration unless intentionally updating it.

---

## 💬 Communication

* Use GitHub Issues or Discussions to propose major changes before working on them.
* Assign reviewers and tag related issues in your PRs (`Closes #issue-number`).
* For urgent changes to `staging` or `main`, communicate with the team and follow approval workflows.

---

## 🙌 Thank You

We appreciate your interest and effort in improving this project. Your contributions help keep the repo reliable, scalable, and production-ready.

