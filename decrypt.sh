#!/bin/sh

# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$JSON_PASS" \
--output app/google-services.json app/google-services.json.gpg
