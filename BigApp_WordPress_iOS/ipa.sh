#!/bin/ksh

#--------------------------------------------
# 功能：为BigWords打包
# 作者：四木
# 创建日期：2015/07/29
#--------------------------------------------

###########################################参数校验###########################################

if [ $1 != "sit" -a $1 != "pre" -a $1 != "prd" ]; then
	echo "请输入正确的构建环境：[sit | pre | prd]"
	exit
else
	build_env=$(echo $1 | perl -pe 's/.*/\u$&/')	
fi

###########################################更新代码###########################################
base_dir=$(dirname $0)
cd ${base_dir}
echo "======开始更新本地代码======"
svn up

###########################################常量设置###########################################

#工程绝对路径
project_path=$(pwd)
echo "======工程路径：${project_path}======"
#创建保存打包结果的目录
result_path=${project_path}/build/${build_env}_$(date +%Y-%m-%d_%H_%M)
mkdir -p "${result_path}"
echo "======最终打包路径：${result_path}======"

#工程配置文件路径
project_name=$(ls | grep xcodeproj | awk -F.xcodeproj '{print $1}')
echo "======project名称：${project_name}======"
target_name=${project_name}
echo "======target名称：${target_name}======"
info_plist=${project_path}/YZWpClient/Info.plist
echo "======Info.plist路径：${info_plist}======"

#取版本号
bundleShortVersion=$(/usr/libexec/PlistBuddy -c "print CFBundleShortVersionString" ${info_plist})
echo "======版本号：${bundleShortVersion}======"

###########################################证书检查###########################################

#检查工程中证书的选择
echo "======检查是否选择了正确的企业证书======"
setting_out=${result_path}/build_setting.txt
xcodebuild -target "${target_name}" -configuration Release -showBuildSettings > ${setting_out}
codeSign=$(grep "CODE_SIGN_IDENTITY" "${setting_out}" | cut  -d  "="  -f  2 | grep -o "[^ ]\+\( \+[^ ]\+\)*")
rightDistributionSign="iPhone Distribution: Suning Appliance Co., Ltd."
if [ "${codeSign}" != "${rightDistributionSign}" ]; then
	echo -e "\033[31m 错误的证书:${codeSign}，请进入xcode选择证书为:${rightDistributionSign} \033[0m"
	exit
fi
#检查授权文件
echo "======检查是否选择了正确的签名文件======"
provisionProfile=$(grep "PROVISIONING_PROFILE[^_]" "${setting_out}" | cut  -d  "="  -f  2 | grep -o "[^ ]\+\( \+[^ ]\+\)*")
rightProvision="c59f649f-edb5-4a18-a4fe-870eb7b52d8d"   #这个是企业证书的id
if [ "${provisionProfile}" != "${rightProvision}" ]; then
	echo -e "\033[31m 错误的签名，请进入xcode重新选择授权文件 \033[0m"
	exit
fi

###########################################修改配置###########################################

#修改应用标识
bundle_identifier=com.youzu.${build_env}
/usr/libexec/PlistBuddy -c "Set :CFBundleIdentifier ${bundle_identifier}" ${info_plist}

#修改Build版本
bundleVersion=$(/usr/libexec/PlistBuddy -c "print CFBundleVersion" ${info_plist})
build_version=`expr ${bundleVersion} + 1`
/usr/libexec/PlistBuddy -c "Set :CFBundleVersion ${build_version}" ${info_plist}

#修改应用名称
bundle_name=${build_env}${bundleShortVersion}.${build_version}
/usr/libexec/PlistBuddy -c "Set :CFBundleDisplayName ${bundle_name}" ${info_plist}

#修改环境配置
config_file=${project_path}/YZWpClient/Src/Constants/BuildConfig.h

if [ -f ${config_file} ]; then

	upper_env=$(echo ${build_env} | tr '[a-z]' '[A-Z]')

	echo "======修改环境配置======"
	sed -i '' "/TARGET_ENV_/ s/1/0/" ${config_file}
	sed -i '' "/TARGET_ENV_${upper_env}/ s/0/1/" ${config_file}

	sed -i '' "/DEBUG_ENABLE/ s/1/0/" ${config_file}
	
fi

###########################################编译打包###########################################

#打测试包
echo "======打${build_env}环境的测试包======"

xcodebuild -target "${target_name}" -configuration Release clean

#编译工程
xcodebuild -target "${target_name}" -configuration Release build

build_result=$?

svn revert ${config_file}

# 编译失败
if [ ${build_result} -ne 0 ]; then
	
	svn revert ${info_plist}

	echo -e "\033[31m 编译失败，请修正后重新构建! \033[0m"

	exit
else

	svn ci ${info_plist} -m "IPA: Update build version."

fi

#build文件夹路径
build_path=${project_path}/build/Release-iphoneos
echo "======编译路径：${build_path}======"
#打包完的程序目录
appDir=${build_path}/${target_name}.app
#dSYM的路径
dsymDir=${build_path}/${target_name}.app.dSYM/Contents/Resources/DWARF/${target_name}

#ipa名称
ipa_name=${target_name}_${bundleShortVersion}.ipa
ipa_path="${result_path}/${ipa_name}"
#先打第一个appStore渠道的包
xcrun -sdk iphoneos PackageApplication -v "${appDir}" -o "${ipa_path}"
#拷贝过来.app和.app.dSYM放在子目录
cp -R "${dsymDir}" "${result_path}/${target_name}.dSYM"

#info名称
ipa_info=${target_name}_${bundleShortVersion}.info
echo ${target_name} ${bundleShortVersion}.${build_version} ${upper_env}($(date "+%Y-%m-%d %H:%M")) > ${result_path}/${ipa_info}


###########################################文件上传###########################################

echo "======开始上传测试包======"

ftp -n <<!
open localhost
user zhoutl 3262212277
binary
cd ${build_env}/ipa
lcd ${result_path}
prompt
mput ${ipa_name} ${ipa_info}
close
bye
!
