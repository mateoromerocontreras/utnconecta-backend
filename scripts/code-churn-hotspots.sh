#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./scripts/code-churn-hotspots.sh [since]
#
# Examples:
#   ./scripts/code-churn-hotspots.sh "30 days ago"
#   ./scripts/code-churn-hotspots.sh "2026-01-01"
#
# Output columns:
#   commits  file
#
# Notes:
# - Focuses on "churn" (how often files change), a proxy for hotspots.
# - Combine with Sonar complexity to prioritize refactors (high churn + high complexity).

since="${1:-"90 days ago"}"

git rev-list --since="$since" --all --name-only \
  | awk 'NF' \
  | sort \
  | uniq -c \
  | sort -nr \
  | awk '{count=$1; $1=""; sub(/^ /,""); print count "\t" $0}' \
  | head -n 50

