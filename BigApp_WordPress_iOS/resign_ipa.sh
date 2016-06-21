#!/bin/sh
# help="please input ipa name"
# help_pro="please input provision name like "



# if [ -z $1 ]; then
# echo $help_pro
# exit -1
# fi
# provision_name=$1

# if [ -z $2 ]; then
# echo $help_pro
# exit -1
# fi
ipa_name="Clan"

provision_name="embedded.mobileprovision"
provision_name_tmp="${provision_name}_tmp"
cp ./$provision_name ./$provision_name_tmp
distribution_name="iPhone Distribution: Su zhou Zhengyou Network Technology Co. Ltd."
echo "reset ipa name $ipa_name"
unzip $ipa_name.ipa


##################################################替换资源文件 start ####################################
target_info_path="$(pwd)/Payload/${ipa_name}.app/Info.plist"
echo "=======target_info_path:${target_info_path}======="

/usr/libexec/PlistBuddy -c "Set :CFBundleDisplayName 哈哈OK了" ${target_info_path}

iconname_array=("AppIcon29x29@2x.png" "AppIcon29x29@3x.png" "AppIcon40x40@2x.png" "AppIcon40x40@3x.png" "AppIcon60x60@2x.png" "AppIcon60x60@3x.png")
iconsize_array=("58 58" "87 87" "80 80" "120 120" "120 120" "180 180")

#图片资源文件路径
images_xcassets_path="$(pwd)/Payload/${ipa_name}.app"
echo "=======images_xcassets_path : ${images_xcassets_path}=======";

for ((i=0;i<${#iconname_array[@]};++i)); do
cp "icon.png" ${images_xcassets_path}/${iconname_array[i]}
sips -s format png ${images_xcassets_path}/${iconname_array[i]} --out ${images_xcassets_path}/${iconname_array[i]}
sips -z ${iconsize_array[i]} ${images_xcassets_path}/${iconname_array[i]}
done


#改变启动图尺寸并移动启动图
launchname_array=("LaunchImage-700-568h@2x.png" "LaunchImage-700@2x.png" "LaunchImage-800-667h@2x.png" "LaunchImage-800-Portrait-736h@3x.png")
launchsize_array=("960 640" "1136 640" "1334 750" "2208 1242")



for ((i=0;i<${#launchname_array[@]};++i)); do
launch_image_name_png="${images_xcassets_path}"/"${launchname_array[i]}"

cp "launch.png"  "${launch_image_name_png}"
sips -s format png  "${launch_image_name_png}" --out  "${launch_image_name_png}"
sips -z ${launchsize_array[i]}   "${launch_image_name_png}"
done



##################################################替换资源文件 end ####################################

rm -rf ./Payload/$ipa_name.app/_CodeSignature
cp ./$provision_name ./Payload/$ipa_name.app/embedded.mobileprovision
/usr/libexec/PlistBuddy -x -c "print :Entitlements " /dev/stdin <<< $(security cms -D -i Payload/*.app/embedded.mobileprovision) > entitlements.plist
#input project build setttings [Code Signing Resource Rules Path] = $(SDKROOT)/ResourceRules.plist
/usr/bin/codesign -f -s "$distribution_name" --resource-rules ./Payload/$ipa_name.app/ResourceRules.plist --entitlements entitlements.plist ./Payload/$ipa_name.app

zip -qr New_$ipa_name.ipa Payload
echo "---------rm $provision_name"
rm ./$provision_name
echo "---------rm Payload"
rm -rf ./Payload
echo "---------rm Symbols"
rm -rf ./Symbols
echo "---------rm entitlements.plist"
rm  ./entitlements.plist
echo "---------mv $provision_name_tmp to $provision_name"
mv ./$provision_name_tmp ./$provision_name

echo "done"