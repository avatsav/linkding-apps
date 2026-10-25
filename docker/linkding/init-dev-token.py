"""Provision the local development token from .env without logging its value."""

# Runs inside the Linkding image; its Python packages are not installed on the host.
# pyright: reportMissingImports=false

import os
import re

from bookmarks.models import ApiToken
from django.contrib.auth import get_user_model
from django.core.management.base import CommandError

key = os.environ["LINKDING_TEST_API_TOKEN"]
if re.fullmatch(r"[0-9a-f]{40}", key) is None:
    raise CommandError(
        "LINKDING_TEST_API_TOKEN must be exactly 40 lowercase hexadecimal characters."
    )

user = get_user_model().objects.get(username=os.environ["LD_SUPERUSER_NAME"])
ApiToken.objects.update_or_create(
    user=user,
    name="Linkding apps development",
    defaults={"key": key},
)
print("Local development API token configured.")
