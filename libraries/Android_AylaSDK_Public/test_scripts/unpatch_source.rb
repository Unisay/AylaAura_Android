#!/usr/bin/ruby
# This script is to recover SDK java source code to its production state.
# The reason why we need to patch source code for Robolectric testing is given in patch_source.rb
# Note: this test-related changes can not be checked into our production source code repository.
#
# 08/12/2016 AylaNetowrks Inc.

target_files_with_parameter = [
    ["../library/src/main/java/com/aylanetworks/aylasdk/AylaNetworks.java", "network"],
    ["../library/src/main/java/com/aylanetworks/aylasdk/AylaDSManager.java", "network"],
    ["../library/src/main/java/com/aylanetworks/aylasdk/AylaDeviceManager.java", "network"],
    ["../library/src/main/java/com/aylanetworks/aylasdk/lan/AylaLanModule.java", "_aylaLocalNetwork"]
]

# remove:
#    import com.android.volley.ResponseDelivery;
#    import com.android.volley.ExecutorDelivery;
#    import java.util.concurrent.Executors;
# change:
#    final ResponseDelivery responseDelivery = new ExecutorDelivery(Executors.newSingleThreadExecutor());
#    xyz = new RequestQueue(cache, network, 4, responseDelivery);
# to:
#    xyz  = new RequestQueue(cache, network);
def unpatch_requestqueue_for_file(pFile)
    file_contents = File.read(pFile[0])
		# remove imports added by the patch
    file_contents.gsub!(/(.*?)package (.*?)\nimport com.android.volley.ResponseDelivery;\nimport com.android.volley.ExecutorDelivery;\nimport java.util.concurrent.Executors;\n(.*)/, "\\1package \\2\n\\3")
		# remove the added statement
    file_contents.gsub!(/(.*?)\n([\s]+)final ResponseDelivery responseDelivery = new ExecutorDelivery\(Executors.newSingleThreadExecutor\(\)\);\n(.*)/, "\\1\n\\3")
		# change the statment back to original
    file_contents.gsub!(/(.*?)new RequestQueue\(cache, #{pFile[1]}, 4, responseDelivery\);\n(.*)/, "\\1new RequestQueue(cache, #{pFile[1]});\\2")
    File.write(pFile[0], file_contents)
end

target_files_with_parameter.each do |f|
    unpatch_requestqueue_for_file(f)
end

