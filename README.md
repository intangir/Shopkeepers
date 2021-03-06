Shopkeepers
===========

Shopkeepers is a Bukkit plugin which allows you to set up custom villager shopkeepers that sell exactly what you want them to sell and for what price. 
You can set up admin shops, which have infinite supply, and you can also set up player shops, which pull supply from a chest.

BukkitDev Page: http://dev.bukkit.org/bukkit-plugins/shopkeepers/  
Forums: http://nisovin.com/forums/index.php?forums/shopkeepers/  
Dev builds: http://ci.cube-nation.de/job/Shopkeepers/

Guidelines
----------

All "volatile" code (any code that relies on CraftBukkit, NMS or specific Bukkit versions) should be in the compat package.
Please keep this code to a minimum wherever possible, as adding more volatile code makes the updating process more difficult.
If it is possible to create a non-volatile fallback method, please do so and put it in the FailedHandler class.

For any major feature changes, please make a forum thread about it and/or create an extra branch so it can be discussed before you commit the code to the main repository.

Build with Maven
----------------

This is the recommended and easy way to compile the plugin yourself and/or help to contribute to it.
Just check out the project to your machine and import it in Eclipse with **Import > Maven > Existing Maven Project**.
Then just right click the imported project and select **Run As > Maven install**.

Build without Maven
-------------------

If you really want to do it the old school way, you're free to import the project in Eclipse with **Import > General > Existing Project into Workspace**. You'll find that the project will instantly show some errors because it's missing its' dependencies. You also need to make sure that you'll include the provided modules (NMSHandlers).

**Here's how you do that:**
* after importing the project right click on it and select **Properties**
* under **Java Build Path > Source** click on **Add Folder...** and add all provided modules:
  * modules/v1_6_R3/src/main/java
  * modules/v1_7_R1/src/main/java
  * ...
* under **Java Build Path > Library** click on **Add External JARs...** and add the needed CraftBukkit.jar files for the modules above<br>
  (you can download them from http://dl.bukkit.org/downloads/craftbukkit)
* after that you can create the plugin for example by using Rightclick and selecting **Export > Java > JAR file** or another recommened way


Todo
----
* Don't overwrite previously stored Attribute data of items in the save file, if we run on an not yet supported server version (and can't read that attribute data). Instead simple keep this data, to not break all items until an update is available which can interpret it again.
* Improve chest protection (the anti-hopper code is inefficient).
* Add MySQL support (maybe someday, certainly not urgent).
* Sign shopkeepers somehow support non-wall signs (sign posts), maybe if a sign is targeted use that sign?
* 1.9 changes: Inventory.getContents() no includes armor contents. Skip those armor slots when searching/removing/adding items? Maybe also skip 'extra slots' (off-hand) and only use Inventory.getStorageContents()
* missing mob types MC 1.8: ENDERMITE, GUARDIAN, RABBIT, ARMOR_STAND
* missing mob types MC 1.9: SHULKER

Ideas
----
* Per-Trade/Shopkeeper settings, maybe via written books:<br>
  -> by adding another row to the shopkeeper-editor inventory window each trade option and shopkeeper could have a slot for a written-book<br>
  -> which could contain additional meta-data, per-trade/shopkeeper settings, which could be used (ex. by other plugins) to trigger certain actions when a specific trade is used <br>
* Maybe move shop options (like currently name, profession, etc.) into a separate inventory view to have additional space there<br>
