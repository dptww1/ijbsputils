ijbsputils
==========

This project is an IntelliJ plugin that supports functionality useful when working on
[Brightspot CMS](https://github.com/perfectsense/brightspot-cms).

Building
--------
I hope to eventually Maven-ize this, but for now it's copy of my local IntelliJ project.  It probably won't build for you as
is, as I haven't included the .idea directory.  Maybe I should have.

For that reason, this repo includes the ijbsputils.jar build result.

Built and tested using IntelliJ 14.0.3.  On OSX, this version runs under Apple's Java 6 runtime, so
when you build the extension, make sure to target that.  Getting that working took longer than I
expected. ([IntelliJ's explanation](https://intellij-support.jetbrains.com/entries/23455956-Selecting-the-JDK-version-the-IDE-will-run-under))

Installing
----------
In IntelliJ, select **Preferences...**, then **Plugins**, then click **Install plugin from disk...**.
Navigate to and choose ijbsputils.jar, then **OK** out of the dialog.  You'll need to restart IntelliJ.

Features
--------

This plugin adds a new *BSP* menu to the IDE, with the following commands:

#### Go To Renderer

If you have focus on a Java class containing a @Renderer.Path annotation (perhaps indirectly
in a superclass), "Go To Renderer" will open the renderer file for you in the editor.  If your
class has multiple @Renderer.Path annotations, you'll be prompted (with an incredibly ugly UI) for which
one you want to go to.

If the file doesn't exist, you can opt to have the plug-in create it for you.

The renderer can be a JSP file or a Freemarker (".ftl") file.  If you have a file named "taglibs" with the
same extension, that file will be included when the renderer is
created.

#### Go To Model

If you have focus on a JSP or FTL file, "Go To Model" will open the corresponding Java file
containing the current file as a @Renderer.Path.  If there are multiple possibilities, you'll be given
a choice of which to navigate to.

Bugs/Issues
-----------
[Bug list](https://github.com/dptww1/ijbsputils/issues)
