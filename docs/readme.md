# JavaSpec GitHub Page

Sources for the main [JavaSpec website][javaspec].

[javaspec]: https://javaspec.info

## Configuration

### Jekyll

* `_config.yml`: Jekyll configuration.  Note that URLs should be written using
  the custom domain name and path (or lack thereof) that will be used to access
  the website, not it's raw location on GitHub Pages (e.g. https://javaspec.info
  over `<username>.github.io`).

### GitHub Pages

* `CNAME`: Custom domain name.  No paths, protocols, or newlines.

## Deployment

This site is built and deployed with [GitHub Actions][github-page-action] to a
custom domain on [GitHub Pages][github-docs-pages].

[github-docs-pages]: https://docs.github.com/en/pages
[github-page-action]: https://github.com/kkrull/javaspec/actions/workflows/pages/pages-build-deployment

## Development Environment

This is a Jekyll project that runs on the version of Ruby listed in
`.ruby-version`.  Install that version of Ruby with `ruby-install`.  Use
[`chruby`](https://github.com/postmodern/chruby) to manage your system and Gem
paths, so that you are using the intended version of Ruby for development.  If
you are using `oh-my-zsh`, there is a [`chruby` plugin][github-omz-chruby] for
that.

Source: https://jekyllrb.com/docs/installation/

[github-omz-chruby]: https://github.com/ohmyzsh/ohmyzsh/blob/master/plugins/chruby/chruby.plugin.zsh

## Integrations

### DNS Provider

This site is hosted on a custom domain, so that domain's DNS provider needs to be configured with:

* Apex records (`A` and `AAAA`) that anchor the custom domain to the IPv4 and
  IPv6 addresses used by GitHub Pages.
* `CNAME` record(s) to alias `javaspec.info` and `www.javaspec.info` to the
  domain on which the content actually resides (`<username>.github.io`).  The
  `www` variant is needed too, or GitHub will complain.

### GitHub

Settings -> Pages: Tell GitHub the branch and path within the repository to use
for GitHub Pages.  In this case, it's `main` and `docs/` so that the website can
be kept up to date on the same branch as the code itself.

## Running locally

```shell
bundle exec jekyll serve
```

Source:
https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll
