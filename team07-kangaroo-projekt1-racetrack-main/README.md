# team07-kangaroo-projekt1-racetrack

## Branching Strategy

To ensure organization and efficiency in the development of our project, we adopt a straightforward branching strategy based on the following workflow:

### Main Branch: `main`
- **Description**: The `main` branch is our primary branch containing stable code that is production-ready.
- **Management**: Direct commits to `main` are not allowed. All changes must be introduced via pull requests from dedicated feature branches.

### Feature Branches
- **Creation**: Each new feature should be developed in a specific branch, created from the `main` branch. The name of the feature branch should correspond to the issue ID on GitHub to facilitate tracking, for example, `feature_#123`.
- **Usage**: These branches are dedicated to the development of individual features or fixes, directly linked to open issues on GitHub.
- **Pull Request (PR)**: Once the feature is completed, a PR is created towards the `main` branch. The PR should be linked to the reference issue to facilitate review of the work done.
- **Review**: Every PR requires a review by at least one other team member before merging. This ensures that the code is scrutinized, keeping the quality and consistency of the project high.

### Managing Pull Requests
- **Review Procedures**: Reviewers should check that the code is well-structured, follows coding best practices, and that all new features are adequately tested.
- **Merging**: Only PRs that receive approval can be merged into `main`. Once merged, the related feature branch is deleted to keep the repository clean.

By incorporating this branching strategy, we aim to optimize the development process and continuously improve the quality of our software.
