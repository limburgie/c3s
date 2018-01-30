# c3s
C3S is a site builder that simplifies the creation and maintenance of content managed websites.
C3S is like CMS, but with a twist. The "3" in C3S stands for the 3 decoupled services of C3S:
* Multi-site registry
* Master service that holds the definition of pages and templates.
* Content service that interfaces with existing cloud-based CMSes, such as Prismic.io and Contentful.

## TL;DR... How does this work?

1. In the site registry, you map a virtual host name to a master repository (e.g. the path of a web server).
2. In the master repository, you define the page structure using a JSON file. The pages themselves are Freemarker templates that use the content api to retrieve content.
3. The content service is a Prismic.io or Contentful repository.
