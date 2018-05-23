# C3S

C3S is a website builder that simplifies the creation and maintenance of content managed websites. It was designed with the following goals in mind:

* Provide a way to host a complex CMS driven website on a simple web server without any special server requirements (no Java, no PHP).
* Provide a way to decouple the structure of a website and its contents where:
  * the site structure is defined by page templates and configuration files, without implementing any Java or Javascript.
  * content is managed outside the platform through well established headless content providers.
* Make a generic content query language that abstracts and simplifies access to content inside the (verbose) underlying content provider.

## Who should use C3S?

* Back-end developers who grasp every opportunity to avoid writing a single line of Javascript.
* Developers who want to use a CMS but don't want to host a Drupal, Wordpress or (god forbid) a Java based CMS.
* Developers who want to be able to switch to another CMS without having to rewrite their website.
* Developers who want to be in full control of the site structure & theming, but at the same time delegate any content management to webmasters.

## TL;DR... How does this work?

In C3S, you first write a `c3s.json` file that defines the structure of your new website and the parameters to connect to the content API. E.g. suppose your site only has two pages (home and news) and its content is hosted by a Prismic.io repository:

```json
{
   "name": "INR Diary",
   "indexPageFriendlyUrl": "home",
   "templateEngine": "FREEMARKER",

   "contentRepositoryConnection": {
      "type": "PRISMIC",
      "repositoryId": "https://inrdiary.prismic.io/api"
   },

   "templates": [
      {
         "name": "base",
         "contents": "templates/base.ftl"
      }
   ],

   "pages": [
      {
         "friendlyUrl": "home",
         "name": "Home",
         "template": "base",
         "inserts": {
            "body": "pages/home.ftl"
         }
      },
      {
         "friendlyUrl": "news",
         "name": "News",
         "template": "base",
         "inserts": {
            "body": "pages/news.ftl"
         }
      }
   ]
}
```

If you look closely at the `c3s.json` file, you can see that both pages use the same template (`base`). This page template looks something like this:

```ftl
<!DOCTYPE html>
<html>
    <head>
        <title>${site.name}</title>
    </head>
    <body>
        <ul>
            <#list site.pages as page>
                <li>
                    <a href="${page.friendlyUrl}">
                        ${page.name}
                    </a>
                </li>
            </#list>
        </ul>
        ${inserts.body}
    </body>
</html>
```

This is basically plain HTML with template variables. The templating language is Freemarker. Other templating languages are possible as well, but Freemarker is the most powerful one.

Notice the `${inserts.body}` variable inside the `base` template. This is actually a placeholder that can be replaced with file contents as defined in the `c3s.json` file.

E.g. the definition of the news page inside `c3s.json` looks like this:

```json
{
    "friendlyUrl": "news",
    "name": "News",
    "template": "base",
    "inserts": {
        "body": "pages/news.ftl"
    }
}
```

In this case, `${inserts.body}` gets replaced with the contents of the `pages/news.ftl` file. This file looks something like this:

```ftl
<#assign items = api.query("news").orderByDesc("date").findAll(5)>
<#list items as item>
    <article>
        <h2>${item.getText("title")}</h2>
        <img src="${items.getImage("banner").url}"/>
        ${item.getStructuredText("body").html}
    </article>
</#list>
```

What this means is that the content provider (in this case a Prismic.io repository) will be queried for the 5 most recent content items of type "news", ordered by date. It will then display title, banner and body text for those articles.

And that's it! Your basic site with templating and content querying is finished!