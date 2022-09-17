# GNU Privacy Guard (`gnupg`)

Sign artifacts with GnuPG.

Install [GNU Privacy Guard][gnupg] to get the `gpg` command, which is used to
sign artifacts (JARs, POMs).  MacOS Homebrew users can install the `gnupg`
package as follows:

```shell
$ brew install gnupg
```

After installing `gpg`, you need to
[generate a key pair][sonatype-gpg-generate-keys] and publish your public key:

```shell
# Generate a public/private key pair and export the private key
$ gpg --gen-key
$ gpg --armor --export-secret-keys
```

You can now use the keys to generate and verify a signature for a file:

```shell
# Sign some arbitrary file
$ gpg -ab README.md #Creates README.md.asc

# Verify the signature
$ gpg --verify README.md.asc
gpg: assuming signed data in 'README.md'
gpg: Signature made ...
gpg: Good signature from ...
```

You will also need to configure Gradle to use these keys, so that it can sign
artifacts.  This can be accomplished by adding properties to your personal
`~/.gradle/gradle.properties` (create a new file if necessary), or by following
one of these [other methods][gradle-signing-credentials].  Either way, you need
to define the following properties:

```ini
#$HOME/.gradle/gradle.properties

# Run: gpg --armor --export-secret-keys
# ASCII-armored private key, including comments
signing.key=...

# Passphrase used to encrypt private key during gpg --gen-keys
signing.passphrase=...
```

Finally if you wish to distribute artifacts, you need to send your public key to
a key server:

```shell
# Look up your public key so you can publish it
$ gpg --list-keys
$ gpg --keyserver keyserver.ubuntu.com --send-keys <key from listing above>
```

[gnupg]: https://www.gnupg.org/
[gradle-signing-credentials]: https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
[sonatype-gpg-generate-keys]: https://central.sonatype.org/publish/requirements/gpg/#generating-a-key-pair
