#!/usr/bin/ruby
# This script is to patch SDK java source code before running Robolectric tests
# The reason is because Robolectric does not have good support for handler to post a Runnable to be executed in another thread.
# Since Volley's RequestQueue uses handler from Main Looper by default to receive runnable, we need to change that to not use
# MainLoper, instead, pass Executors.newSingleThreadExecutor() to Volley RequestQueue() method so that it will not post Runnable
# to MainLooper but call execute() method to run the Runnable directly.
#
# 08/12/2016 AylaNetowrks Inc.

# this script can only be run form its current directory
# these files are to be patched followed by its 2nd RequestQuene method call paraemter
target_files_with_parameter = [
    ["../library/src/main/java/com/aylanetworks/aylasdk/AylaNetworks.java", "network"],
    ["../library/src/main/java/com/aylanetworks/aylasdk/AylaDSManager.java", "network"],
    ["../library/src/main/java/com/aylanetworks/aylasdk/AylaDeviceManager.java", "network"],
    ["../library/src/main/java/com/aylanetworks/aylasdk/lan/AylaLanModule.java", "_aylaLocalNetwork"]
]

# add:
#    import com.android.volley.ResponseDelivery;
#    import com.android.volley.ExecutorDelivery;
#    import java.util.concurrent.Executors;
# change:
#    xyz  = new RequestQueue(cache, network);
# to:
#    final ResponseDelivery responseDelivery = new ExecutorDelivery(Executors.newSingleThreadExecutor());
#    xyz = new RequestQueue(cache, network, 4, responseDelivery);
def patch_requestqueue_for_file(pFile)
    file_contents = File.read(pFile[0])
		# add imports required by the patch
    file_contents.gsub!(/(.*?)package (.*?)\n(.*)/, "\\1package \\2\nimport com.android.volley.ResponseDelivery;\nimport com.android.volley.ExecutorDelivery;\nimport java.util.concurrent.Executors;\n\\3")
		# patch new RequestQueue statment
    file_contents.gsub!(/(.*?)([\S]+) = new RequestQueue\(cache, #{pFile[1]}\);(.*)/, "\\1final ResponseDelivery responseDelivery = new ExecutorDelivery(Executors.newSingleThreadExecutor());\n        \\2 = new RequestQueue(cache, #{pFile[1]}, 4, responseDelivery);\n\\3")
    File.write(pFile[0], file_contents)
end

target_files_with_parameter.each do |f|
    patch_requestqueue_for_file(f)
end

