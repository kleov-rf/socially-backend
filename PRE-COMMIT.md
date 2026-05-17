# Pre-commit Setup

## Quick Start

1. **Install pre-commit**:
   ```bash
   pip install pre-commit
   ```

2. **Install the hooks**:
   ```bash
   pre-commit install
   pre-commit install --hook-type commit-msg
   ```

3. **Test it** (optional):
   ```bash
   pre-commit run --all-files
   ```

## What This Does

### On Every Commit:
- Removes trailing whitespace
- Ensures files end with newline
- Checks YAML syntax
- Prevents large files (>1000KB)
- Detects private keys
- Normalizes line endings to LF

### When Java Files Change:
- Runs Spotless check (`./gradlew spotlessCheck`)
- Runs unit tests (`./gradlew test`)
- Compiles main and test sources (`./gradlew compileJava compileTestJava`)

### On Commit Message:
- Validates conventional commit format

## Commit Message Format

Your commit messages should follow:
```
[taskId] type(scope): description

Examples:
[ABC123] feat(donations): add create donation use case
[XYZ789] fix(auth): correct token refresh expiry
[DEF456] docs(readme): update local setup guide
```

**Valid types**: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`

## Troubleshooting

If hooks fail, you can:
- Fix the issue and commit again
- Skip hooks (not recommended): `git commit --no-verify`

Existing clones that already had pre-commit installed only need:
```bash
pre-commit install --hook-type commit-msg
```

## Requirements

- Python 3.8+
- Java and Gradle (via `./gradlew`)
