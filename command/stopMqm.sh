pid=$(ps -ef|grep "java -Xms512m -Xmx512m -jar mqm.jar" |cut -c 9-26)
#echo $pid

ppid1=$(echo $pid|cut -d " " -f 2)

ppid2=$(echo $pid|cut -d " " -f 4)

if [ $ppid1 -a $ppid1 -eq "1" ]
then
	killedId=$(echo $pid|cut -d " " -f 1)
else
	ppid1=0
fi

if [ $ppid2 -a $ppid2 -eq "1" ]
then
        killedId=$(echo $pid|cut -d " " -f 3)
else
	ppid2=0
fi

if [ $killedId ]
then
	kill -9 $killedId
	echo "info:pid="$killedId" had been killed successfully."
else
	echo "warn:not found PID."
	echo "please make sure MQM application is running." 
fi
