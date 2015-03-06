# Extension Sorter
Given a directory that contains a mix of artifacts, sort the artifacts by extension
groups but keep subdirectory structure.

If we start with:

```
./media/2014/01/09/b.FOO
./media/2014/01/09/a.foo
./media/2014/01/09/d.baz
./media/2014/01/09/c.bar
./media/2014/01/15/15.FOO
./media/2014/01/15/15.baz
./media/2014/01/15/15.bar
./media/2014/01/15/15.foo
./media/2015/01/21/2015.baz2
./media/2015/01/21/2015.foo2
./media/2015/01/21/2015.FOO
./media/2015/01/21/2015.foo
```

And use extension groups: foo,FOO and baz

We end up with the following under our destination directory:

```
./foo/2015/01/21/2015.FOO
./foo/2015/01/21/2015.foo
./foo/2014/01/09/a.foo
./foo/2014/01/09/b.FOO
./foo/2014/01/15/15.foo
./foo/2014/01/15/15.FOO
./baz/2014/01/09/d.baz
./baz/2014/01/15/15.baz
./other/2015/01/21/2015.foo2
./other/2015/01/21/2015.baz2
./other/2014/01/09/c.bar
./other/2014/01/15/15.bar
```

The foo subdirectory (first entry in foo extension group) contains all files
that had extensions listed in the foo extension group under their original
subdirectory structure relative to the sort directory root. So foo subdirectory
contains foo and FOO files.

baz subdirectory contains all .baz files.

other directory contains files that did not match any of our extension groups.

The original directory is untouched.

## How to use Extension Sorter

#### Amazon example:

One of the Amazon Prime perks is to be able to back up any pictures to their
cloud for free. But what if you stored all your media files in a date-oriented
structure that mixes pictures with movies and other artifacts?

Use extension-sorter!

### Extension Sorter command line arguments

Extension sorter requires three arguments:
1. --sortDir - this is the source dir that contains the files we want to sort.
1. --destDir - the destination directory where the artifacts will be stored sorted.
1. --ext - (one or more sets of these). Comma separated list(s) of extension groups to
   be sorted into the same subdirectory. The very first entry is also used as the
   subdirectory name.

The following command

```
    java -jar ~/extsorter.jar --sortDir media --destDir sorted \
       --ext jpg,tif,JPG,CR2,png,gif,GIF,PNG,cr2,jpeg \
       --ext zip,ZIP \
       --ext mov,m4v,MOV,MPG,avi,wmv \
       --ext wav,WAV \
```

sorts all files in the media subdirectory (relative to where the command is
run from) into the sorted subdirectory. Under sorted we will have:

```
sorted/jpg - contains all .jpg,tif,JPG,CR2,png,gif,GIF,PNG,cr2,jpeg files
sorted/zip - contains all .zip or .ZIP files
sorted/mov - contains all .mov,m4v,MOV,MPG,avi,wmv files
sorted/wav - contains all .wav or .WAV files
sorted/other - any files with extensions not in one of our extension groups, for example .doc, .bin or .txt.
```

The original subdirectory for every file relative to media is maintained. So

    media/2014/12/06/nikolaus.jpg

will end up in

    sorted/jpg/2014/12/06/nikolaus.jpg

## Building extension-sorter

Extension-sorter uses Java to run and sbt to build. See [Java JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
downloads for Java and
[SBT Installation Instructions](http://www.scala-sbt.org/release/tutorial/Setup.html)
on how to install SBT.

Once installed, run:

    git clone git@github.com:medale/extension-sorter.git  
    cd extension-sorter
    sbt assembly
    cp target/scala-2.11/extension-sorter-assembly-1.0.jar ~/extsorter.jar

This first clones the extension-sorter git repo to extension-sorter and then
builds a uber/fat jar with all required dependencies at
target/scala-2.11/extension-sorter-assembly-1.0.jar. We copy that file to our
home directory as extsorter.jar.

The main class that gets run is com.uebercomputing.extsort.Sorter. The sbt
assembly plugin marked that as Main-Class in META-INF/MANIFEST.MF:

    Main-Class: com.uebercomputing.extsort.Sorter

So we can execute that class via our executable jar:

    java -jar ~/extsorter.jar --sortDir <sortDir> --destDir <destDir> --ext <comma-separated extensions1> --ext <extensions2>
