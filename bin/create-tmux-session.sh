#!/usr/bin/env bash

set -e

if (( $# != 1 ))
then
  echo "Usage: $0 <session name>"
  echo "Example: $0 javaspec"
  exit 1
fi

session_name="$1"
tmux new -d -s "$session_name" -n git \
  \; new-window -n rake

echo "Created session: $session_name"
echo "Attach with: tmux attach -t $session_name"

