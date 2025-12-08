for port in 9000 8092 8090 8093 8089 3000; do
  pid=$(lsof -ti tcp:$port)
  if [ -n "$pid" ]; then
    echo "Killing process $pid on port $port"
    kill -9 $pid
  else
    echo "Port $port is free"
  fi
done
