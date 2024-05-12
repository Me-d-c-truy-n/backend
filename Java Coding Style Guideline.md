Java Coding Style Guideline
===========================

**Version:** 2.0

**Author:** Thibaud LEPRETRE

**Greatly inspired by (thanks guys):**

* [Oracle Code convention for Java](http://www.oracle.com/technetwork/java/index-135089.html)
* [Google Java style](https://google.github.io/styleguide/javaguide.html)

**Table of Contents**

- [1 Source file basics](#1-source-file-basics)
  - [1.1 File name](#11-file-name)
  - [1.2 File encoding](#12-file-encoding)
- [2 Source file structure](#2-source-file-structure)
  - [2.1 License or copyright information](#21-license-or-copyright-information)
  - [2.2 Package statement](#22-package-statement)
    - [2.2.1 No line-wrapping](#221-no-line-wrapping)
  - [2.3 Import statements](#23-import-statements)
    - [2.3.1 No wildcard imports](#231-no-wildcard-imports)
    - [2.3.2 No line-wrapping](#232-no-line-wrapping)
    - [2.3.3 Ordering and spacing](#233-ordering-and-spacing)
  - [2.4 Class and Interface declaration](#24-class-and-interface-declaration)
    - [2.4.1 One top-level class declaration](#241-one-top-level-class-declaration)
    - [2.4.2 Class member ordering](#242-class-member-ordering)
    - [2.4.3 Overloads: never split](#243-overloads-never-split)
- [3 Formatting](#3-formatting)
  - [3.1 Indentation](#31-indentation)
    - [3.1.1 Alignement using 4 spaces](#311-alignement-using-4-spaces)
    - [3.1.2 Why not using tabs/smartab?](#312-why-not-using-tabssmartab)
  - [3.2 Lines](#32-lines)
    - [3.2.1 Line length](#321-line-length)
    - [3.2.2 Wrapping lines](#322-wrapping-lines)
  - [3.3 Braces](#33-braces)
    - [3.3.1 Nonempty blocks](#331-nonempty-blocks)
    - [3.3.2 Empty blocks](#332-empty-blocks)
  - [3.4 Whitespace](#34-whitespace)
    - [3.4.1 Vertical Whitespace](#341-vertical-whitespace)
    - [3.4.2 Horizontal whitespace](#342-horizontal-whitespace)
    - [3.4.3 Horizontal alignment](#343-horizontal-alignment)
  - [3.5 Declarations](#35-declarations)
    - [3.5.1 Variables](#351-variables)
      - [3.5.1.1 One variable per declaration](#3511-one-variable-per-declaration)
      - [3.5.1.2 Initialization](#3512-initialization)
    - [3.5.2 Classes and Interfaces](#352-classes-and-interfaces)
  - [3.6 Statements](#36-statements)
    - [3.6.1 Simple statements](#361-simple-statements)
    - [3.6.2 Compound statements](#362-compound-statements)
    - [3.6.3 `return` statements](#363-return-statements)
    - [3.6.4 `if`, `if-else`, `if else-if else` statements](#364-if-if-else-if-else-if-else-statements)
    - [3.6.5 `for` statements](#365-for-statements)
    - [3.6.6 `while` statements](#366-while-statements)
    - [3.6.7 `do-while` statements](#367-do-while-statements)
    - [3.6.8 `switch` statements](#368-switch-statements)
      - [3.6.8.1 Indentation](#3681-indentation)
      - [3.6.8.2 Fall-through: commented](#3682-fall-through-commented)
      - [3.6.8.3 Default case](#3683-default-case)
    - [3.6.9 `try-catch` statements](#369-try-catch-statements)
  - [3.7 Specific constructs](#37-specific-constructs)
    - [3.7.1 Enum classes](#371-enum-classes)
    - [3.7.2 Annotations](#372-annotations)
    - [3.7.3 Arrays](#373-arrays)
      - [3.7.3.1 Array initializers](#3731-array-initializers)
      - [3.7.3.2 No C-style array declarations](#3732-no-c-style-array-declarations)
    - [3.7.4 Comments](#374-comments)
    - [3.8 Modifiers](#38-modifiers)
    - [3.9 Numeric Literals](#39-numeric-literals)
    - [3.10 Runtime Exception](#310-runtime-exception)
- [4 Naming](#4-naming)
  - [4.1 Common to all identifiers](#41-common-to-all-identifiers)
  - [4.2 By indentifier type](#42-by-indentifier-type)
    - [4.2.1 Package names](#421-package-names)
    - [4.2.2 Class names](#422-class-names)
    - [4.2.3 Method names](#423-method-names)
    - [4.2.4 Constant names](#424-constant-names)
    - [4.2.5 Non-constant field names](#425-non-constant-field-names)
    - [4.2.6 Parameter names](#426-parameter-names)
    - [4.2.7 Local variable names](#427-local-variable-names)
    - [4.2.8 Type variable names](#428-type-variable-names)
  - [4.3 Camel case](#43-camel-case)
- [5 Programming Practices](#5-programming-practices)
  - [5.1 Avoid public class members](#51-avoid-public-class-members)
  - [5.2 Static members: qualified using class](#52-static-members-qualified-using-class)
  - [5.3 Variable Assignments](#53-variable-assignments)
  - [5.4 Grouping parentheses](#54-grouping-parentheses)
  - [5.5 Returning Values](#55-returning-values)
  - [5.6 Expressions before '?' in the Conditional Operator](#56-expressions-before--in-the-conditional-operator)
  - [5.7 @Override: always used](#57-@override-always-used)
  - [5.8 Caught exceptions](#58-caught-exceptions)
- [6 Javadoc](#6-javadoc)
  - [6.1 Formatting](#61-formatting)
    - [6.1.1 General form](#611-general-form)
    - [6.1.2 Paragraphs](#612-paragraphs)
    - [6.1.3 At-clauses](#613-at-clauses)
  - [6.2 The summary fragment](#62-the-summary-fragment)
  - [6.3 Where Javadoc is used](#63-where-javadoc-is-used)
    - [6.3.1 Exception: self-explanatory methods](#631-exception-self-explanatory-methods)
    - [6.3.2 Exception: overrides](#632-exception-overrides)



1 Source file basics
--------------------

### 1.1 File name

The source file name consists of the case-sensitive name of the top-level class it contains.

### 1.2 File encoding

Source files are encoded in **UTF-8**.

2 Source file structure
-----------------------

A source file consists of, in order:

1. License or copyright information, if present
2. Package statement
3. Import statements
4. Exactly one top-level class

**Exactly one blank line separates** each section that is present.

### 2.1 License or copyright information

If license or copyright information belongs in a file, it belongs here.

### 2.2 Package statement

The first non-comment line of most Java source files is a `package` statement.

#### 2.2.1 No line-wrapping
 
The `package` statement is **not line-wrapped**. The [section 4.2.1 Line length](#421-line-length) does not apply to package statements.

### 2.3 Import statements

#### 2.3.1 No wildcard imports

**Wildcard imports**, static or otherwise, **are not used**.

#### 2.3.2 No line-wrapping

The `import` statement is not **line-wrapped**. The [section 4.2.1 Line length](#421-line-length) does not apply to import statements.

#### 2.3.3 Ordering and spacing

Import statements are divided into the following groups, in this order, with each group separated by a single blank line:

1. `com.kakawait` imports (only if this source file is in the `com.kakawait` package space)
2. Third-party imports, one group per top-level package, in ASCII sort order
   * for example: `android`, `com`, `junit`, `org`, `sun`
3. `java` imports
4. `javax` imports
5. All static imports in a single group

Within a group there are no blank lines, and the imported names appear in ASCII sort order. (Note: this is not the same as the import statements being in ASCII sort order; the presence of semicolons warps the result.)

Example:

```java
package com.kakawait.service.auth.adapter;

import com.kakawait.service.AuthenticationService;
import com.kakawait.service.auth.Authentication;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.easymock.EasyMock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.SSLPeerUnverifiedException;

import static org.assertj.core.api.Assertions.assertThat;
```

### 2.4 Class and Interface declaration

#### 2.4.1 One top-level class declaration

Each top-level class resides in a source file of its own.

#### 2.4.2 Class member ordering

The ordering of the members of a class can have a great effect on learnability, but there is no single correct recipe for how to do it. Different classes may order their members differently.

What is important is that each class order its members in ***some* logical order**, which its maintainer could explain if asked. For example, new methods are not just habitually added to the end of the class, as that would yield "chronological by date added" ordering, which is not a logical ordering.

You can optionally adopt the following arrangement (which place public method before all other to separate API from implementation):

1. `field public static`
2. `field protected static`
3. `field package static`
4. `field private static`
5. `field public`
6. `field protected`
7. `field package`
8. `field private`
9. `constructor`
10. `method public static`
11. `method public`
12. `method non-public static`
13. `method non-public`
14. `inner enum`
15. `inner interface`
16. `inner class static`
17. `inner class`
 
#### 2.4.3 Overloads: never split

When a class has multiple constructors, or multiple methods with the same name, these appear sequentially, with no intervening members.

Moreover keep getter and setter together.


3 Formatting
------------

### 3.1 Indentation

Each time a new block or block-like construct is opened, the indent increases by tab. When the block ends, the indent returns to the previous indent level. The indent level applies to both code and comments throughout the block.

#### 3.1.1 Alignement using 4 spaces

Use 4 spaces for expressing the indentation level and for alignment.

Example using indentation equals to four spaces:

```java
int f(int x, int y) {
....return g(x, y);
}
```

#### 3.1.2 Why not using tabs/smartab?

[Tabs are evil](http://www.emacswiki.org/emacs-zh-cn/TabsAreEvil).

### 3.2 Lines

#### 3.2.1 Line length

Avoid lines longer than 120 characters. Except as noted below, any line that would exceed this limit must be line-wrapped, as explained in [Section 3.2.2 Wrapping lines](#422-wrapping-lines).

**Exceptions:**

1. Lines where obeying the column limit is not possible (for example, a long URL in Javadoc, or a long JSNI method reference).
2. `package` and `import` statements (see [Section 3.2 Package statement](#32-package-statement) and [Section 3.3 Import statements](#33-import-statements)).
3. Command lines in a comment that may be cut-and-pasted into a shell.

#### 3.2.2 Wrapping lines

When line-wrapping, each line after the first (each continuation line) is indented at least +8 (2 times indentation) from the original line.

When there are multiple continuation lines, indentation may be varied beyond +8 (2 times indentation) as desired. In general, two continuation lines use the same indentation level if and only if they begin with syntactically parallel elements.

There is no comprehensive, deterministic formula showing exactly how to line-wrap in every situation. Very often there are several valid ways to line-wrap the same piece of code.

General principles can be used:

* Break after a comma.
* Break before an operator.
* Prefer higher-level breaks to lower-level breaks.
* Align the new line with the beginning of the expression at the same level on the previous line.
* If the above rules lead to confusing code or to code that's squished up against the right margin, just indent two times instead.

> **Tip:** extracting a method or local variable may solve the problem without the need to line-wrap.

Here are some examples of breaking method calls:

```java
// AVOID
someMethod(longExpression1, longExpression2, longExpression3
           , longExpression4, longExpression5);

// ALLOWED
someMethod(longExpression1, longExpression2, longExpression3, 
        longExpression4, longExpression5);

// ALLOWED
someMethod(longExpression1, longExpression2, longExpression3, 
           longExpression4, longExpression5);
```

Following are two examples of breaking an arithmetic expression. The first is preferred, since the break occurs outside the parenthesized expression, which is at a higher level.

```java
// AVOID
longName1 = longName2 * (longName3 + longName4
            - longName5) + 4 * longname6;

// PREFER
longName1 = longName2 * (longName3 + longName4 - longName5)
            + 4 * longname6;
```

Following are two examples of indenting method declarations. The first is the conventional case. The second would shift the second and third lines to the far right if it used conventional indentation, so instead it indents only 8 spaces.

```java
// CONVENTIONAL INDENTATION
someMethod(int anArg, Object anotherArg, String yetAnotherArg,
           Object andStillAnother) {
    ...
}

// INDENT 2 TIME TO AVOID VERY DEEP INDENTS
private static synchronized horkingLongMethodName(int anArg,
        Object anotherArg, String yetAnotherArg,
        Object andStillAnother) {
    ...
}
```

Line wrapping for if statements should generally use the 2 tabs rule, since conventional (1 tab) indentation makes seeing the body difficult. For example:

```java
// AVOID
if ((condition1 && condition2)
    || (condition3 && condition4)
    || !(condition5 && condition6)) {
    doSomethingAboutIt();            // MAKE THIS LINE EASY TO MISS
} 

// USE THIS INDENTATION INSTEAD
if ((condition1 && condition2)
        || (condition3 && condition4)
        || !(condition5 && condition6)) {
    doSomethingAboutIt();
} 

// OR USE THIS
if ((condition1 && condition2) || (condition3 && condition4)
        || !(condition5 && condition6)) {
    doSomethingAboutIt();
}
```

Here are three acceptable ways to format ternary expressions:

```java
alpha = (aLongBooleanExpression) ? beta : gamma;  

alpha = (aLongBooleanExpression) ? beta
                                 : gamma;  

alpha = (aLongBooleanExpression)
        ? beta 
        : gamma;
```

### 3.3 Braces

#### 3.3.1 Nonempty blocks
 
Braces follow the *Kernighan and Ritchie* style (*Egyptian brackets*) for *nonempty* blocks and block-like constructs:

* No line break before the opening brace.
* Line break after the opening brace.
* Line break before the closing brace.
* Line break after the closing brace if that brace terminates a statement or the body of a method, constructor or *named* class. For example, there is no line break after the brace if it is followed by `else` or a comma.

Example:

```java
return () -> {
  while (condition()) {
    method();
  }
};

return new MyClass() {
    @Override 
    public void method() {
        if (condition()) {
            try {
                something();
            } catch (ProblemException e) {
                recover();
            }
        } else if (otherCondition()) {
            somethingElse();
        } else {
            lastThing();
        }
    }
};
```

#### 3.3.2 Empty blocks

An empty block or block-like construct may be closed immediately after it is opened, with no characters or line break in between (`{}`), unless it is part of a multi-block statement (one that directly contains multiple blocks: `if/else-if/else` or `try/catch/finally`).

Example:

```java
// AVOID
void doNothing() {

}

// AVOID
void doNothing()
{}

// ALLOWED
void doNothing() {
}

// PREFER
void doNothing() {}
```

For multi-block statement:

```java
// AVOID
if (test) {
    ...
} else {}

// AVOID
if (test) {}
else {
    ...
}

// ALLOWED
if (test) {
    ...
} else {

}

// PREFER
if (test) {
    ...
} else {
    // Add comment to explain why the block is empty
}
```

### 3.4 Whitespace

#### 3.4.1 Vertical Whitespace

A single blank line appears:

1. Between consecutive members (or initializers) of a class: fields, constructors, methods, nested classes, static initializers, instance initializers.
    * **Exception**: a blank line between two consecutive fields (having no other code between them) is optional. Such blank lines are used as needed to create logical groupings of fields.
    * **Exception**: Blank lines between enum constants are covered in [Section 4.7.1 Enum classes](#471-enum-classes).
2. Between statements, as *needed* to organize the code into logical subsections.
3. Between `switch` cases, see [Section 4.6.8 `switch` statements](#468-switch-statements).
4. Optionally before the first member or after the last member of the class (neither encouraged nor discouraged).
5. As required by other sections of this document, such as [Section 3.3 Import statements](#33-import-statements).

*Multiple* consecutive blank lines are permitted, but never required (or encouraged).

#### 3.4.2 Horizontal whitespace

Beyond where required by the language or other style rules, and apart from literals, comments and Javadoc, a single ASCII space also appears in the following places only.

1. Separating any reserved word, such as `if`, `for` or `catch`, from an open parenthesis (`(`) that follows it on that line
2. Separating any reserved word, such as `else` or `catch`, from a closing curly brace (`}`) that precedes it on that line
3. Before any open curly brace (`{`), with two exceptions:
    * `@SomeAnnotation({a, b})` (no space is needed)
    * `String[][] x = {{"foo"}};` (no space is required between `{{`)
4. On both sides of any binary or ternary operator. This also applies to the following "operator-like" symbols:
    * the ampersand in a conjunctive type bound: `<T extends Foo & Bar>`
    * the pipe for a catch block that handles multiple exceptions: `catch (FooException | BarException e)`
    * the colon (`:`) in an enhanced `for` ("foreach") statement
    * the arrow in a lambda expression: `(String str) -> str.length()`
  but not
    * the two colons (`::`) of a method reference, which is written like `Object::toString`
    * the dot separator (`.`), which is written like `object.toString()`
5. After `,:;` or the closing parenthesis (`)`) of a cast
6. On both sides of the double slash (`//`) that begins an end-of-line comment. Here, multiple spaces are allowed, but not required.
7. Between the type and variable of a declaration: `List<String> list`
8. *Optional* just inside both braces of an array initializer
    * `new int[] {5, 6}` and `new int[] { 5, 6 }` are both valid

This rule never requires or forbids additional space at the start or end of a line; it addresses only *interior* space.

#### 3.4.3 Horizontal alignment

This practice is permitted, but is **never required**. It is not even required to maintain horizontal alignment in places where it was already used.

Here is an example without alignment, then using alignment:

```java
private int x; // this is fine
private Color color; // this too

private int   x;      // permitted, but future edits
private Color color;  // may leave it unaligned
```

> **Tip:** Alignment can aid readability, but it creates problems for future maintenance. Consider a future change that needs to touch just one line. This change may leave the formerly-pleasing formatting mangled, and that is **allowed**. More often it prompts the coder (perhaps you) to adjust whitespace on nearby lines as well, possibly triggering a cascading series of reformattings. That one-line change now has a "blast radius." This can at worst result in pointless busywork, but at best it still corrupts version history information, slows down reviewers and exacerbates merge conflicts.

### 3.5 Declarations

#### 3.5.1 Variables

##### 3.5.1.1 One variable per declaration

Every variable declaration (field or local) declares only one variable: declarations such as `int a, b;` are **not used**.

##### 3.5.1.2 Initialization

Local variables are not habitually declared at the start of their containing block or block-like construct. Instead, local variables are declared close to the point they are first used (within reason), to minimize their scope. Local variable declarations typically have initializers, or are initialized immediately after declaration.

#### 3.5.2 Classes and Interfaces

When coding Java classes and interfaces, the following formatting rules should be followed:

* No space between a method name and the parenthesis (`(`) starting its parameter list.
* Open brace (`{`) appears at the end of the same line as the declaration statement.
* Closing brace (`}`) starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the (`}`) should appear immediately after the (`{`).
* Methods are separated by a blank line.

```java
class Sample extends Object {
    int ivar1;
    int ivar2;
    
    Sample(int i, int j) {
        ivar1 = i;
        ivar2 = j;
    }
    
    int emptyMethod() {}
    ...
}
```

### 3.6 Statements

#### 3.6.1 Simple statements

Each line should contain at most one statement. Example:

```java
argv++;         // CORRECT
argc++;         // CORRECT
argv++; argc--; // AVOID
```

#### 3.6.2 Compound statements 

Compound statements are statements that contain lists of statements enclosed in braces (`{ statements }`). See the following sections for examples.

* The enclosed statements should be indented one more level than the compound statement.
* The opening brace should be at the end of the line that begins the compound statement; the closing brace should begin a line and be indented to the beginning of the compound statement.
* Braces are used around all statements, even single statements, when they are part of a control structure, such as a `if-else` or `for` statement. This makes it easier to add statements without accidentally introducing bugs due to forgetting to add braces.

#### 3.6.3 `return` statements

A returnstatement with a value should not use parentheses unless they make the return value more obvious in some way. Example:

```java
return;
return myDisk.size();
return (size ? size : defaultSize);
```

#### 3.6.4 `if`, `if-else`, `if else-if else` statements

The `if-else` class of statements should have the following form:

```java
// if
if (condition) {
    statements;
}

// if-else
if (condition) {
    statements;
} else {
    statements;
}

//if else-if else
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
```

> Note: if statements always use braces `{}`. Avoid the following error-prone form:

```java
//AVOID! THIS OMITS THE BRACES {}!
if (condition)
    statement;
```

#### 3.6.5 `for` statements

A `for` statement should have the following forms:

```java
// Standard
for (initialization; condition; update) {
    statements;
}

// For-each way
for (variable : collection) {
    statements;
}
```

When using the comma operator in the *initialization* or *update* clause of aforstatement, avoid the complexity of using more than three variables. If needed, use separate statements before the `for` loop (for the *initialization* clause) or at the end of the loop (for the *update* clause).

An empty `for` statement (one in which all the work is done in the *initialization*, *condition*, and *update* clauses) should have the following form:

```java
for (initialization; condition; update);
```

#### 3.6.6 `while` statements

A `while` statement should have the following form:

```java
while (condition) {
    statements;
}
```

An empty `while` statement should have the following form:

```java
while (condition);
```

#### 3.6.7 `do-while` statements

A `do-while` statement should have the following form:

```java
do {
    statements;
} while (condition);
```

#### 3.6.8 `switch` statements

A `switch` statement should have the following form:

```java
switch (condition) {
    case ABC:
        statements;
        // falls through
    case DEF:
        statements;
        break;
    case XYZ:
        statements;
        break;
    default:
        statements;
        break;
}
```

Vertical whitespaces are allowed:

```java
switch (condition) {
    case ABC:
        statements;
        // falls through

    case DEF:
        statements;
        break;

    case XYZ:
        statements;
        break;

    default:
        statements;
        break;
}
```

##### 3.6.8.1 Indentation

As with any other block, the contents of a switch block are indented +4.

After a switch label, a newline appears, and the indentation level is increased +4, exactly as if a block were being opened. The following switch label returns to the previous indentation level, as if a block had been closed.

##### 3.6.8.2 Fall-through: commented

Every time a case falls through (doesn’t include a `break` statement), add a comment where the `break` statement would normally be. This is shown in the preceding code example with the for example: `// falls through` comment.

##### 3.6.8.3 Default case

Every `switch` statement should include a `default` case. The `break` in the `default` case is redundant, but it prevents a fall-through error if later another `case` is added.

#### 3.6.9 `try-catch` statements

A `try-catch` statement should have the following forms:

```java
try {
    statements;
} catch (ExceptionClass e) {
    statements;
}

try (resource) {
    statements;
}
```

A `try-catch` statement may has multiple redundant `catch` clause:

```java
// AVOID
try {
    statements;
} catch (ExceptionClass e) {
    logger.error(e.getMessage(), e);
} catch (OtherExceptionClass e) {
    logger.error(e.getMessage(), e);
}

// PREFER
try {
    statements;
} catch (ExceptionClass | OtherExceptionClass e) {
    logger.error(e.getMessage(), e);
}
```

A `try-catch` statement may also be followed by `finally`, which executes regardless of whether or not the `try` block has completed successfully

```java
try {
    statements;
} catch (ExceptionClass e) {
    statements;
} finally {
    statements;
}
```

### 3.7 Specific constructs

#### 3.7.2 Annotations

Annotations applying to a class, method or constructor appear immediately after the documentation block, and each annotation is listed on a line of its own (that is, one annotation per line). Example:

```java
@Override
@Nullable
public String getNameIfPresent() { ... }
```

**Exception**: A single parameterless annotation may instead appear together with the first line of the signature, for example:

```java
@Override public int hashCode() { ... }
```

There are no specific rules for formatting parameter and local variable annotations.

#### 3.7.3 Arrays

##### 3.7.3.1 Array initializers

Any array initializer may optionally be formatted as if it were a "block-like construct." For example, the following are all valid (**not** an exhaustive list):

```java
new int[] {           new int[] {
    0, 1, 2, 3            0,
}                         1,
                          2,
new int[] {               3,
    0, 1,             }
    2, 3
}                     new int[]
                          {0, 1, 2, 3}
```

##### 3.7.3.2 No C-style array declarations

The square brackets form a part of the *type*, not the variable: `String[] args`, not `String args[]`.

#### 3.7.4 Comments

Block comments are indented at the same level as the surrounding code. They may be in `/* ... */` style or `// ...` style. For multi-line `/* ... */` comments, subsequent lines must start with `*` aligned with the * on the previous line.

```java
/*
 * This is          // And so
 * okay.            // is this.
 */
```

Comments are not enclosed in boxes drawn with asterisks or other characters.

> **Tip:** When writing multi-line comments, use the `/* ... */` style if you want automatic code formatters to re-wrap the lines when necessary (paragraph-style). Most formatters don't re-wrap lines in `// ...` style comment blocks.

#### 3.8 Modifiers

Class and member modifiers, when present, appear in the order recommended by the Java Language Specification:

```java
public protected private abstract default static final transient volatile synchronized native strictfp
```

#### 3.9 Numeric Literals

`long`-valued integer literals use an uppercase `L` suffix, never lowercase (to avoid confusion with the digit 1). For example, `3000000000L` rather than `3000000000l`.

#### 3.10 Runtime Exception

`RuntimeException` or any **unchecked** exception should not declared on signature since it is misleading to the user of that API.

Declaring in javadoc is recommended if possible.

4 Naming
--------

### 4.1 Common to all identifiers 

Only ASCII letters and digits (and in two cases, noted below, underscores). Identifier name have to match with the regexp `\w+`.

Special prefixes or suffixes, like those seen in the examples `name_`, `mName`, `s_name` and `kName`, are **not** used

### 4.2 By indentifier type

#### 4.2.1 Package names 

The prefix of a unique package name is always written in all-lowercase ASCII letters and should be one of the top-level domain names, currently com, edu, gov, mil, net, org or one of the English two-letter codes identifying countries as specified in ISO Standard 3166, 1981.

Package names are all lowercase, with consecutive words simply concatenated together (no underscores). For example,

```java 
// AVOID 
COM.kakawait.Service
// GOOD
com.kakawait.service
```

#### 4.2.2 Class names

Class names are written in [UpperCamelCase](#53-camel-case).

Class names are typically nouns or noun phrases. For example, `Character` or `ImmutableList`. Interface names may also be nouns or noun phrases (for example, `List`), but may sometimes be adjectives or adjective phrases instead (for example, `Readable`).

There are no specific rules or even well-established conventions for naming annotation types.

Test classes are named starting with the name of the class they are testing, and ending with Test. For example, `HashTest` or `HashIntegrationTest`

#### 4.2.3 Method names

Method names are written in [lowerCamelCase](#53-camel-case).

Method names are typically verbs or verb phrases. For example, `sendMessage` or `stop`.

Underscores may appear in `JUnit` test method names to separate logical components of the name. One typical pattern is `<UnitOfWork>_<StateUnderTest>_<ExpectedBehavior>`, for example `Sum_NegativeNumberAs1stParam_ExceptionThrown`. There is no One Correct Way to name test methods.

#### 4.2.4 Constant names

Constant names use `CONSTANT_CASE`: all uppercase letters, with words separated by underscores. But what is a constant, exactly?

Every constant is a static final field, but not all static final fields are constants. Before choosing constant case, consider whether the field really *feels like* a constant. For example, if any of that instance's observable state can change, it is almost certainly not a constant. Merely intending to never mutate the object is generally not enough. Examples

```java
// Constants
static final int NUMBER = 5;
static final ImmutableList<String> NAMES = ImmutableList.of("Ed", "Ann");
static final Joiner COMMA_JOINER = Joiner.on(',');  // because Joiner is immutable
static final SomeMutableType[] EMPTY_ARRAY = {};
enum SomeEnum { ENUM_CONSTANT }

// Not constants
static String nonFinal = "non-final";
final String nonStatic = "non-static";
static final Set<String> mutableCollection = new HashSet<String>();
static final ImmutableSet<SomeMutableType> mutableElements = ImmutableSet.of(mutable);
static final Logger logger = Logger.getLogger(MyClass.getName());
static final String[] nonEmptyArray = {"these", "can", "change"};
```

These names are typically nouns or noun phrases.

#### 4.2.5 Non-constant field names

Non-constant field names (static or otherwise) are written in [lowerCamelCase](#53-camel-case).

These names are typically nouns or noun phrases. For example, `computedValues` or `index`.

#### 4.2.6 Parameter names

Parameter names are written in [lowerCamelCase](#53-camel-case).

One-character parameter names should be avoided.

#### 4.2.7 Local variable names

Local variable names are written in lowerCamelCase, and can be abbreviated more liberally than other types of names.

However, one-character names should be avoided, except for temporary and looping variables.

Even when final and immutable, local variables are not considered to be constants, and should not be styled as constants.

#### 4.2.8 Type variable names

Each type variable is named in one of two styles:

* A single capital letter, optionally followed by a single numeral (such as `E`, `T`, `X`, `T2`)
* A name in the form used for classes (see [Section 4.5.2 Classes and Interfaces](#452-Classes-and-Interfaces)), followed by the capital letter `T` (examples: `RequestT`, `FooBarT`). 

### 4.3 Camel case

Sometimes there is more than one reasonable way to convert an English phrase into camel case, such as when acronyms or unusual constructs like "IPv6" or "iOS" are present. To improve predictability use the following (nearly) deterministic scheme.

1. Convert the phrase to plain ASCII and remove any apostrophes. For example, "Müller's algorithm" might become "Muellers algorithm".
2. Divide this result into words, splitting on spaces and any remaining punctuation (typically hyphens).
    * *Recommended*: if any word already has a conventional camel-case appearance in common usage, split this into its constituent parts (e.g., "AdWords" becomes "ad words"). Note that a word such as "iOS" is not really in camel case *per se*; it defies any convention, so this recommendation does not apply.
3. Now lowercase *everything* (including acronyms), then uppercase only the first character of:
    * ... each word, to yield *upper camel case*, or
    * ... each word except the first, to yield *lower camel case*
4. Finally, join all the words into a single identifier.

Note that the casing of the original words is almost entirely disregarded. Examples:

Prose form | Correct | Incorrect
---------- | ------- | ---------
"XML HTTP request" | `XmlHttpRequest` | `XMLHTTPRequest`
"new customer ID" | `newCustomerId` | `newCustomerID`
"inner stopwatch" | `innerStopwatch` | `innerStopWatch`
"supports IPv6 on iOS?" | `supportsIpv6OnIos` | `supportsIPv6OnIOS`
"YouTube importer" | `YouTubeImporter`* | 

\* Can also accept `YoutubeImporter` but not recommended

> Note: Some words are ambiguously hyphenated in the English language: for example "nonempty" and "non-empty" are both correct, so the method names `checkNonempty` and `checkNonEmpty` are likewise both correct.

5 Programming Practices
-----------------------

### 5.1 Avoid public class members

Don’t make any instance or class variable public without good reason. Often, instance variables don’t need to be explicitly set or gotten—often that happens as a side effect of method calls.

One example of appropriate public instance variables is the case where the class is essentially a data structure, with no behavior. In other words, if you would have used a `struct` instead of a class (if Java supported `struct`), then it’s appropriate to make the class’s instance variables public.

### 5.2 Static members: qualified using class

When a reference to a static class member must be qualified, it is qualified with that class's name, not with a reference or expression of that class's type.

```java
Foo aFoo = ...;
Foo.aStaticMethod(); // good
aFoo.aStaticMethod(); // bad
somethingThatYieldsAFoo().aStaticMethod(); // very bad
```

### 5.3 Variable Assignments

Avoid assigning several variables to the same value in a single statement. It is hard to read. Example:

```java
fooBar.fChar = barFoo.lchar = 'c'; // AVOID!
```

Do not use the assignment operator in a place where it can be easily confused with the equality operator. Example:

```java
// AVOID
if (c++ = d++) {
    ...
}
```

should be written as

```java
if ((c++ = d++) != 0) {
    ...
}
```

Do not use embedded assignments in an attempt to improve run-time performance. This is the job of the compiler. Example:

```java
// AVOID
d = (a = b + c) + r;
```

should be written as
 
```java
a = b + c;
d = a + r;
```

### 5.4 Grouping parentheses

It is generally a good idea to use parentheses liberally in expressions involving mixed operators to avoid operator precedence problems. Even if the operator precedence seems clear to you, it might not be to others—you shouldn’t assume that other programmers know precedence as well as you do.

```java
// AVOID
if (a == b && c == d)

// PREFER
if ((a == b) && (c == d))
```

### 5.5 Returning Values

Try to make the structure of your program match the intent. Example:

```java
if (booleanExpression) {
    return true;
} else {
    return false;
}
```

should instead be written as

```java
return booleanExpression;
```

Similarly,

```java
if (condition) {
    return x;
}
return y;
```

should be written as

```java
return (condition ? x : y);
```

### 5.6 Expressions before '?' in the Conditional Operator

If an expression containing a binary operator appears before the `?` in the ternary `?`: operator, it
should be parenthesized. Example:

```java
(x >= 0) ? x : -x;
``` 

### 5.7 @Override: always used

A method is marked with the `@Override` annotation whenever it is legal. This includes a class method overriding a superclass method, a class method implementing an interface method, and an interface method respecifying a superinterface method.

**Exception**: `@Override` may be omitted when the parent method is `@Deprecated`.

### 5.8 Caught exceptions

Except as noted below, it is very rarely correct to do nothing in response to a caught exception. (Typical responses are to log it, or if it is considered "impossible", rethrow it as an `AssertionError`.)

When it truly is appropriate to take no action whatsoever in a catch block, the reason this is justified is explained in a comment.

```java
try {
    int i = Integer.parseInt(response);
    return handleNumericResponse(i);
} catch (NumberFormatException ok) {
    // it's not numeric; that's fine, just continue
}

return handleTextResponse(response);
```

6 Javadoc
--------

### 6.1 Formatting

#### 6.1.1 General form

The *basic* formatting of Javadoc blocks is as seen in this example:

```java
/**
 * Multiple lines of Javadoc text are written here,
 * wrapped normally...
 */
public int method(String p1) { ... }
```

... or in this single-line example:

```java
/** An especially short bit of Javadoc. */
```

The basic form is always acceptable. The single-line form may be substituted when there are no at-clauses present, and the entirety of the Javadoc block (including comment markers) can fit on a single line.

#### 6.1.2 Paragraphs

One blank line — that is, a line containing only the aligned leading asterisk (`*`) — appears between paragraphs, and before the group of "at-clauses" if present. Each paragraph but the first has `<p>` immediately before the first word, with no space after.

#### 6.1.3 At-clauses

Any of the standard "at-clauses" that are used appear in the order `@param`, `@return`, `@throws`, `@deprecated`, and these four types never appear with an empty description. When an at-clause doesn't fit on a single line, continuation lines are indented four (or more) spaces from the position of the `@`.

### 6.2 The summary fragment

The Javadoc for each class and member begins with a brief summary fragment. This fragment is very important: it is the only part of the text that appears in certain contexts such as class and method indexes.

### 6.3 Where Javadoc is used

At the *minimum*, Javadoc is present for every `public` class, and every `public` or `protected` member of such a class, with a few exceptions noted below.

Other classes and members still have Javadoc as *needed*. Whenever an implementation comment would be used to define the overall purpose or behavior of a class, method or field, that comment is written as Javadoc instead. (It's more uniform, and more tool-friendly.)

#### 6.3.1 Exception: self-explanatory methods

Javadoc is optional for "simple, obvious" methods like `getFoo`, in cases where there really and truly is nothing else worthwhile to say but "Returns the foo".

#### 6.3.2 Exception: overrides

Javadoc is not always present on a method that overrides a supertype method.