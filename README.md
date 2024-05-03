# AXW-ScanExcept
Listing 60 AXW-ScanExcept/ScanExcept._iava Page 102

Cont. from [58.59.HUM-HashEquals](https://github.com/Java-PJATK/58.59.HUM-HashEquals)

# Section 11. Exceptions

Exceptions are situations when program does not know what to do next. For example, we say _read data from this file_, but there is no file to read from. Or, we try to print an element of an array with a given index, but this index is negative or bigger then the size of our array, so the required element doesn’t exist. 

In all such situations, normal flow of control is disrupted and an exception is **thrown**: the JVM creates an object representing the error and checks, if we have supplied a code to somehow handle this kind of error (we will see in a moment, how to supply such code).

All possible exceptions fall into one of two categories:

* Checked exceptions:  these are exceptions that must be somehow dealt with by our program, otherwise the program will not even compile. We can deal with checked exceptions

  1. by using `try-catch` blocks;
  2. by declaring the function in which a given exception may be thrown as throwing it — then the caller of the function will have to handle it.

* Unchecked exceptions: these are exceptions that we can, but not have to, handle. If we don’t handle it and this kind of exception occurs, the program terminates (after printing some information about the exception that has been encountered).

Exceptions are represented by objects of types extending `Throwable` (the top of the hierarchy tree). It has two ‘children’, one of them, `Exception`, represents _checked_ exceptions, except the subtree rooted at `RuntimeException`, which are unchecked.

The second child subtree, rooted at `Error`, represents unchecked exceptions only used internally by JVM; normally, they shouldn’t (although they can, if we insist) be handled at all, as they indicate serious problems beyond our control. 

The general structure of the `Throwable` class hierarchy looks like this (image taken from [javamex.com](http://www.javamex.com) 9), where red color denotes subtrees of _unchecked_ exceptions:

## 11.1 try-catch blocks

How do we handle exceptions? We enclose a fragment of our code where an exception may, at least potentially, occur in a `try` block of the `try-catch` construct:

```java
try {
    // ...
} catch(ExcType1 ex) {
    // handling the exception of type ExcType1
} catch(ExcType2 ex) {
    // handling the exception of type ExcType2
}
// ... other catch blocks
```

The code in the `try` block is executed. If everything goes smoothly and there is no exception, the `catch` blocks are ignored. However, suppose that at some point when executing the code in the `try` block, an exception occurs. Then

* execution of the code in the try block is disrupted;

* object of a class corresponding to the type of the exception is created;

* the type of this object is compared with `ExcType1`; if this type is `ExcType1` or its subclass (i.e., a class extending it directly or indirectly), then the flow of control enters the corresponding `catch` block, the exception is deemed to be
already handled and the code in the `catch` block is executed (inside the block, ex will be the reference to the object representing the exception; such objects carry a lot of information and we can invoke useful methods on them). After that, no other `catch` block will be considered;

* if the type of the exception is not `ExcType1` or its subclass, then, in the same way, the next `catch` block is tried;

* ...and so on, until a catch block with an appropriate type is found.

Note that it doesn’t make sense to place a `catch` block corresponding to exceptions of type `Type2` _after_ the catch block corresponding to `Type1`, if type `Type2` extends `Type1`. This is because exceptions of type `Type2` would be ‘caught’ by the first `catch`, making the second unreachable. For example, there is a class `FileNotFoundException` which is derived (inherits, extends) from `IOException`.

Something like

```java
try {
    // ...
} catch(FileNotFoundException e) {
    // ...
} catch(IOException e) {
    // ...
}
```

does make sense: if FileNotFoundException occurred, then the first catch will be executed, if it was any other exception derived from IOException — the second one.

However

```java
try {
    // ...
} catch(IOException e) {
    // ...
} catch(FileNotFoundException e) { // NO!!!
    // ...
}
```

is wrong, because `FileNotFoundException` and all other exceptions derived form `IOException` will be caught by the first `catch`; the second is unreachable and hence useless.  

It is possible for one `catch` block to handle two (or more) types of unrelated exceptions (they are unrelated if none of them is a subtype of the other). We just specify these types separating their names with a vertical bar (`|`); for example in

```java
    // ...
    catch (IOException | SQLException e) {
        // ...
        throw e;
    // rethrowing exception
    }
}
```

the `catch` will catch exceptions of type `IOException` and of type `SQLException` as well (and their subclasses). 

This example also shows that handling an exception, we can, after doing something, **rethrow** the same exception (or, as a matter of fact, another one) to be handled elsewhere.

## 11.2 finally block

After all `catch` blocks, we can (but we don’t have to) use a **finally block**

```java
try {
    // ...
} catch(ExcType1 e) {
    // ...
} catch(ExcType2 e) {
    // ...
} finally {
    // ...
}
```

The code in `finally` block will always be executed. If there is no error in the `try` block, then all `catch` blocks will be ignored, but `finally` block will be executed; if there is an error in the `try` block, then the corresponding `catch` block (if any) will be executed and, afterwards, the `finally` block (even if there is a return statement in try and/or `catch` blocks!) 

Finally blocks are very useful when we want to ensure that some resources (open files, open internet or data-base connections, locks etc.) are released always — whether an exception occurred or not. It is even possible to use a `finally` block when no exception is anticipated. For example, the code below can be dangerous

```java
    // get resources
    // ...
    if ( condition1 ) return;
    // ...
    if ( condition2 ) return;
    // ...
    // release resources
```

because if any of conditions holds, the resources acquired at the beginning will not be released. However, using `finally`

```java
try {
    // get resources
    // ...
    if ( condition1 ) return;
    // ...
    if ( condition2 ) return;
    // ...
} finally {
    // release resources
}
```

we can ensure that the resources will be released no matter what.

## 11.3 Propagating exceptions

Sometimes we know that an exception (in particular, a checked one) can occur but we don’t know how to handle it. Then we can declare the whole function as throwing this kind of exception (or even, comma separated, exceptions of two or more types):

```java
void fun( /* parameters */ ) throws ExcType1, ExcType2 {
    // here we do   n o t   handle exceptions
    // of types ExcType1, ExcType2
}
```

In this way we propagate the exception ‘up the stack’ to the caller (the function which calls our `fun`), so there the invocation of `fun` will have to be handled

```java
void caller( /* parameters */ ) {
    // ...
    try {
        fun( /* args */ );
    } catch(ExcType1 e) {
        // ...
    } catch(ExcType2 e) {
        // ...
    }
    // ...
}
```

Of course, the caller function can itself be declared as throwing and then exceptions will be propagated to the caller of caller, and so on. In this way, we can even propagate exceptions up to the `main` function, which, although it is not recommended, itself may be declared as throwing (propagating exceptions to the caller of `main`, i.e., the JVM itself).

## 11.4 Throwing exceptions

In some situations, we can throw exceptions by ourselves. Suppose we implement, in class `Person`, a method setting the person’s age

```java
void setAge(int age) {
this.age = age;
}
```

We may decide that trying to set a negative age is a user’s error. We can signal such invalid attempt by throwing an exception of some type. Java defines many types of exceptions in its standard library — in this particular case `IllegalArgumentException` would be probably most appropriate. We thus rewrite out method like this

```java
    void setAge(int age) {
        if (age < 0)
            throw new IllegalArgumentException("age<0");
        this.age = age;
    }
```

This exception is unchecked, so we don’t have to handle it or to declare the method as throwing it. Nevertheless, it is a good practice to document the fact that such an exception might be thrown by adding it to the declaration of the method

```java
    void setAge(int age) throws IllegalArgumentException {
    if (age < 0)
        throw new IllegalArgumentException("age<0");
    this.age = age;
    }
```

As we said, many classes representing exceptions have already be written and are part of the standard library. However, we can also create such classes ourselves. Normally, it is very simple; all we have to do is to define a class extending a library class: a subclass of `RuntimeException` if we want an unchecked exception or a subclass of `Exception` if we want our class to represent a checked exception. All library exception classes have two constructors: the default one and one taking a string representing a message that can be queried on the object. So we can write

```java
    public class MyException extends Exception {
        public MyException() { }
        public MyException(String message) {
        super(message);
        }
    }
```

and that’s basically enough, although, of course, we can add more constructors, fields and methods if we find it appropriate for our purposes. This is seldom necessary, because the main message passed by an exception is just its type.

## 11.5 Examples

In the example below, we handle possible exceptions related to input/output operations. In particular, opening a file and reading from it using a `Scanner` can throw a checked exception (a subtype of `IOException`) that we have to handle. [Note that
reading from System.in does not throw checked exceptions.]

## Listing 60 AXW-ScanExcept/ScanExcept.java

```java
// AXW-ScanExcept/ScanExcept.java
 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ScanExcept {
    public static void main (String[] args) {
          // no checked exception here
        @SuppressWarnings("resource")
        Scanner console = new Scanner(System.in);

        Scanner scfile = null;
        try {
            scfile = new Scanner(Paths.get("file.txt"), "UTF-8");
            int count = 0;
            while (scfile.hasNextLine())
                System.out.printf("%2d: %s%n", ++count,
                                  scfile.nextLine());
        } catch(IOException e) {
            System.out.println("Exception: " + e +
                               "\n**** Now the stack:");
            e.printStackTrace();
            System.out.println("**** CONTINUING...");
        } finally {
            System.out.println("FINALLY executed");
        }

        System.out.println("Enter anything...");
        String s = console.next();
        System.out.println("Read from console: " + s);

          // Active only when run with '-ea' option!
          // Should be used for debugging only.
          // No side effects allowed!
        assert scfile != null : "apparently no file";

        if (scfile != null) scfile.close();
    }
}
```
When the input file file.txt exists, reading it succeeds, the catch clause is not
entered, but the finally clause is executed:

```java
    1: Line 1
    2: Line 2
    3: Line 3
  FINALLY executed
  Enter anything...
  something
  Read from console: something
```

However, when the input file (file.txt) is missing, the FileNotFoundException
thrown, variable scfile remains null and the catch clause is entered. The exception isis
now considered handled, so the program continues (and the finally clause is executed
anyway):

```java
Exception: java.nio.file.NoSuchFileException: file.txt
**** Now the stack:
java.nio.file.NoSuchFileException: file.txt
        at java.base/sun.nio.fs.UnixException
            .translateToIOException(UnixException.java:92)
        at java.base/sun.nio.fs.UnixException
            .rethrowAsIOException(UnixException.java:106)
        at java.base/sun.nio.fs.UnixException
            .rethrowAsIOException(UnixException.java:111)
        at java.base/sun.nio.fs.UnixFileSystemProvider
            .newByteChannel(UnixFileSystemProvider.java:218)
        at java.base/java.nio.file.Files
            .newByteChannel(Files.java:380)
        at java.base/java.nio.file.Files
            .newByteChannel(Files.java:432)
        at java.base/java.nio.file.spi.FileSystemProvider
            .newInputStream(FileSystemProvider.java:422)
        at java.base/java.nio.file.Files
            .newInputStream(Files.java:160)
        at java.base/java.util.Scanner
            .makeReadable(Scanner.java:622)
        at java.base/java.util.Scanner.<init>(Scanner.java:759)
        at java.base/java.util.Scanner.<init>(Scanner.java:741)
        at ScanExcept.main(ScanExcept.java:13)
**** CONTINUING...
FINALLY executed
Enter anything...
something
Read from console: something
Exception in thread "main"
        java.lang.AssertionError: apparently no file
        at ScanExcept.main(ScanExcept.java:34)
```

At the end of the program, we used an assertion statement; we will say a few words about this topic in a moment.

Next [Listing 61 AXX-TryCatch/TryCatch.java](https://github.com/Java-PJATK/61.AXX-TryCatch)
