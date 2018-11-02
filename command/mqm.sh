#init
cur_dir=$(pwd)
#echo current directory:$cur_dir
bin_dir=$cur_dir/bin

chmod 700 $bin_dir/startMqm.sh
chmod 700 $bin_dir/stopMqm.sh
chmod 700 $bin_dir/show.sh
chmod 700 $bin_dir/runTest.sh

#start
startMqm(){
	sh $bin_dir/startMqm.sh
}

#stop
stopMqm(){
	sh $bin_dir/stopMqm.sh
}

#show
showPid(){
	sh $bin_dir/show.sh
}

#test
runTest(){
	sh $bin_dir/runTest.sh
}

case "$1" in
start)
  startMqm
  ;;
stop)
  stopMqm
  ;;
restart)
  stopMqm
  startMqm
  ;;
show)
  showPid
  ;;
test)
  runTest
  ;;
*)
  printf 'Usage: %s {start|stop|restart|show|test}\n' "$prog"
  exit 1
  ;;
esac
