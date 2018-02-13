# New Processor

## Things to install

1. sbt
1. maven (mvn)
1. git
1. NiFi
1. (optional) git UI tool like GitHub Desktop or Sourcetree
1. (optional) IntelliJ community edition

## Start from a template

1. Clone this repository (repo)
1. From the root directory of the repo run: `mvn compile`

## Build the nar

Now that things compile, you can create the `nar`
which is the package of the code to be used by NiFi.

You can find the general documentation for this process here:
https://nifi.apache.org/docs/nifi-docs/html/developer-guide.html#nars

From the root directory of the repo run: `mvn package`

## Deploy the nar

On your local machine, copy the new `nar` to the `lib` directory of NiFi, for example:
`cp nifi-example-processor-nar/target/nifi-example-processor-nar-0.0.1-SNAPSHOT.nar /usr/local/Cellar/nifi/1.5.0/libexec/lib/`

## Start NiFi

In the latest development version of NiFi combined with the latest version of Chrome,
you may run into some issues with hostnames.

1. Find the `nifi.properties` config file
1. Find the line with `nifi.web.http.host=`
1. Update that line to be `nifi.web.http.host=localhost.local`
1. Run `nifi start` from the `bin` directory
1. Edit your `hosts` file to contain `127.0.0.1		localhost.local`
    1. Shameless plug for http://vigilantcupcake.com/ which should make it easier on Windows after you accept the signing error since my cert expired

## Create an instance of your processor

1. Open your browser to `http://localhost.local:8080/nifi/`
1. From the top left in the NiFi menu bar, drag the processor icon into the canvas
1. In the filter box, type `example`
1. Select your new processor and hit `Add` on the bottom right
1. Create a new processor of the type `LogAttribute`
1. Select your example processor and look for a circle with an arrow in it. Drag that to the `LogAttribute` processor
1. In the dialog that appears, select `success` and `failure` under `For Relationships`
1. Select the `LogAttribute` processor and look for the circle with an arrow in it. Drag that back on to the `LogAttribute processor`
1. In the dialog that appears, select `success`
1. Select your example processor and hit the triangle play button on the left
1. Wait about 5 seconds and then hit stop (right click the canvas and select `refresh` if it won't let you)
1. The queue should now have many things in it, right click it and select `List queue`
1. Click the i in a circle on the left side of one of the rows
1. Click the eyeball and `VIEW` button
1. Select `formatted` from the View as drop down

## Explore

Now that you have a working build process and processor you can start to modify the `onTrigger`
function to do other things.

There are many java examples here:
https://github.com/apache/nifi/tree/master/nifi-nar-bundles/nifi-standard-bundle/nifi-standard-processors/src/main/java/org/apache/nifi/processors/standard

And for Scala tips: http://docs.scala-lang.org/tour/basics.html
is a good starting reference point as well as
using IntelliJ with the Scala plugin will provide text completion.

After deploying a new `nar`, you will need to run `nifi restart`
from the nifi `bin` directory.
Additionally you will need to at least clear the queue you filled
or delete all the processors and create the flow again.