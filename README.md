# eXPL
Expression Pattern Language (eXPL) combines two programming paradigms: Logic and Imperative. 
The language executes queries which perform operations with data structures. This is the logic
paradigm component, but expressions are formed using Java syntax and permit procedural operations
characteristic of the imperative paradigm. The data structures provide the patterns that feature
in the name of the lanaguage and they make programming easy, even fun.

## Getting Started

The instructions for getting started can be found at [eXPL Home Page][http://cybersearch2.com.au/logic/classylogic.html].
You will need to clone this project from the [Github site][https://github.com/cybersearch2/eXPL.git] and then use
Maven to install it. Both Java SE version 7 and above and Maven need to be installed in order to proceed. Once
the project is installed you can progress through the [Reference][http://cybersearch2.com.au/logic/reference.html] while 
running the numerous tutorial examples to learn about the eXPL language.

## Classy logic features

All eXPL code is available under the Open Source GPLv3 license. There is a lot of borrowing from Java, such as
expression syntax and identifiers, but also access to components of the Java runtime such as regular expressions
and locales. The language is not strongly typed, but a new currency type is a boon for performing financial
calculations and supporting internationalization. There are also NaN (not a number) and "unknown" types for
dealing with inpcomplete, real-world data.

The eXPL language is terse, with keywords reserved mostly for structural elements, three of which come from
logic programming,: "axiom", "template" and "term". There are also two different selection mechanisms which
very useful and share the keyword "choice".

Something else to look out for are scopes, which are namespaces which can be assigned individual locales and
properties. Scopes facilitate dealing with categories in which different rules, rates etc apply while
applying local formats in the case of international categories. 

## Extensions library

Project [classy_eXPL][https://github.com/cybersearch2/classy_eXPL.git] links in the [Classy Tools lightweight JPA][http://http://cybersearch2.com.au/develop/classytools.html]
package to illustrate how eXPL can work with ORM persistence technology.


