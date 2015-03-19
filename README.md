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
expected.

Installing
----------
In IntelliJ, select **Preferences...**, then **Plugins**, then click **Install plugin from disk...**.
Navigate to and choose ijbsputils.jar, then **OK** out of the dialog.  You'll need to restart IntelliJ.

Features
--------
Not a lot yet.  The plugin creates a new *BSP* menu with a single item, "Go To JSP".
If you currently selected a Java class containing a @Renderer.Path annotation,
"Go To JSP" will open that file for you in the editor.  If your class has multiple
@Renderer.Path annotations, you'll be prompted (with an incredibly ugly UI) for which
one you want to go to.

Bugs/Issues
-----------
[Bug list](https://github.com/dptww1/ijbsputils/issues)
