#!/bin/ksh

#--------------------------------------------
# 功能：为BigWords打包
# 作者：四木
# 创建日期：2015/07/29
#--------------------------------------------

#更新本地代码
base_dir=$(dirname $0)
cd ${base_dir}
echo "======开始更新本地代码======"
svn up

#工程绝对路径
project_path=$(pwd)
echo "======工程路径：${project_path}======"

#创建保存打包结果的目录
result_path=${project_path}/build/Release_$(date +%Y-%m-%d_%H_%M)
mkdir -p "${result_path}"
echo "======最终打包路径：${result_path}======"

#工程配置文件路径
project_name=$(ls | grep xcodeproj | awk -F.xcodeproj '{print $1}')
echo "======project名称：${project_name}======"

target_name=${project_name}
echo "======target名称：${target_name}======"

info_plist=${project_path}/${target_name}/Info.plist
echo "======Info.plist路径：${info_plist}======"

#取版本号
bundleShortVersion=$(/usr/libexec/PlistBuddy -c "print CFBundleShortVersionString" ${info_plist})
echo "======版本号：${bundleShortVersion}======"

#检查工程中证书的选择
echo "======检查是否选择了正确的发布证书======"
setting_out=${result_path}/build_setting.txt
xcodebuild -target "${target_name}" -configuration Release -showBuildSettings > ${setting_out}
codeSign=$(grep "CODE_SIGN_IDENTITY" "${setting_out}" | cut  -d  "="  -f  2 | grep -o "[^ ]\+\( \+[^ ]\+\)*")
rightDistributionSign="iPhone Distribution: ShenZhen LaNvBang Network Technology Co., Ltd. (855M8EKPQE)"
if [ "${codeSign}" != "${rightDistributionSign}" ]; then
    echo -e "\033[31m 错误的证书:${codeSign}，请进入xcode选择证书为:${rightDistributionSign} \033[0m"
    exit
fi

#检查授权文件
echo "======检查是否选择了正确的签名文件======"
provisionProfile=$(grep "PROVISIONING_PROFILE[^_]" "${setting_out}" | cut  -d  "="  -f  2 | grep -o "[^ ]\+\( \+[^ ]\+\)*")
rightProvision="96f0b745-72d1-4070-ad80-4428d8d56ae7"   #这个是发布证书的id
if [ "${provisionProfile}" != "${rightProvision}" ]; then
    echo -e "\033[31m 错误的签名，请进入xcode重新选择授权文件:${rightProvision} \033[0m"
    exit
fi

#检查支持最低系统版本
echo "======检查是否选择了正确的支持最低系统版本======"
deploymentTarget=$(grep "IPHONEOS_DEPLOYMENT_TARGET" "${setting_out}" | cut  -d  "="  -f  2 | grep -o "[^ ]\+\( \+[^ ]\+\)*")
rightDeploymentTarget="7.0"
if [ "${deploymentTarget}" != "${rightDeploymentTarget}" ]; then
    echo -e "\033[31m 错误的最低支持版本${deploymentTarget}，请进入xcode重新选择支持版本 \033[0m"
    exit
fi

#设置bundleIdentifier
bundleIdentifier="com.lanvbang.mobile"
/usr/libexec/PlistBuddy -c "set CFBundleIdentifier ${bundleIdentifier}" ${info_plist}

#设置bundleDisplayName
bundleDisplayName="辣女帮"
/usr/libexec/PlistBuddy -c "Set :CFBundleDisplayName ${bundleDisplayName}" ${info_plist}



###########################################编译打包###########################################

echo "======开始clean工程======"
xcodebuild -target "${target_name}" -configuration Release clean

#编译工程
xcodebuild -target "${target_name}" -configuration Release build

build_result=$?

# 编译失败
if [ ${build_result} -ne 0 ]; then

	echo -e "\033[31m 编译失败，请修正后重新构建! \033[0m"

	exit
fi

#build文件夹路径
build_path=${project_path}/build/Release-iphoneos
echo "======编译路径：${build_path}======"
#打包完的程序目录
appDir=${build_path}/${target_name}.app
#dSYM的路径
dsymDir=${build_path}/${target_name}.app.dSYM/Contents/Resources/DWARF/${target_name}

#ipa名称
ipa_name=${target_name}_${bundleShortVersion}_appStore.ipa
ipa_path="${result_path}/${ipa_name}"
#先打第一个appStore渠道的包
xcrun -sdk iphoneos PackageApplication -v "${appDir}" -o "${ipa_path}"
#拷贝dSYM放在子目录
cp -R "${dsymDir}" "${result_path}/${target_name}.dSYM"

