import asyncio
import websockets
import time
import socket
import sys
import datetime

timeStart = time.time()

async def echo(websocket):

  websocketMessage = '{"Version":"v1.6r1-eu027",\
  "Build":"3d07b461d7a9565cea07727c44760d3b55bb0039",\
  "HardwareBuild":"",\
  "Devices":2,\
  "Connected_Users":6,\
  "DiskBytesFree":29747884032,\
  "UAT_messages_last_minute":0,\
  "UAT_messages_max":0,\
  "ES_messages_last_minute":1286,\
  "ES_messages_max":2026,\
  "OGN_messages_last_minute":0,\
  "OGN_messages_max":0,\
  "OGN_connected":true,\
  "AIS_messages_last_minute":0,\
  "AIS_messages_max":0,\
  "AIS_connected":false,\
  "UAT_traffic_targets_tracking":0,\
  "ES_traffic_targets_tracking":9,\
  "Ping_connected":false,\
  "UATRadio_connected":false,\
  "GPS_satellites_locked":8,\
  "GPS_satellites_seen":11,\
  "GPS_satellites_tracked":11,\
  "GPS_position_accuracy":5.4,\
  "GPS_connected":true,\
  "GPS_solution":"3D GPS",\
  "GPS_detected_type":23,\
  "GPS_NetworkRemoteIp":"",\
  "Uptime":699970,\
  "UptimeClock":"0001-01-01T00:11:39.97Z",\
  "CPUTemp":50.634,\
  "CPUTempMin":34.076,\
  "CPUTempMax":50.634,\
  "NetworkDataMessagesSent":15429,\
  "NetworkDataBytesSent":589491,\
  "NetworkDataMessagesSentLastSec":8,\
  "NetworkDataBytesSentLastSec":264,\
  "UAT_METAR_total":0,\
  "UAT_TAF_total":0,\
  "UAT_NEXRAD_total":0,\
  "UAT_SIGMET_total":0,\
  "UAT_PIREP_total":0,\
  "UAT_NOTAM_total":0,\
  "UAT_OTHER_total":0,\
  "Errors":[],\
  "Logfile_Size":30683,\
  "AHRS_LogFiles_Size":0,\
  "BMPConnected":true,\
  "IMUConnected":true,\
  "NightMode":false,\
  "OGN_noise_db":11.7,\
  "OGN_gain_db":37.2,\
  "OGN_tx_enabled":false}'

  while True:
    try:
      timeNow = time.time() - timeStart
      utcString = time.strftime("%H:%M:%S", time.gmtime(timeNow))
      utcindex = websocketMessage.find("UptimeClock")
      websocketMessage = websocketMessage[:(utcindex+25)] + utcString + websocketMessage[(utcindex+33):]
      await websocket.send(websocketMessage)
      await asyncio.sleep(1)
    except Exception:
      break
		

async def websocket():
  async with websockets.serve(echo, socket.gethostname(), 80):
    await asyncio.Future()

nmeaMessage = ["$GPRMC,145634.60,A,4923.90465,N,00907.39883,E,120,40.0,220722,,,D*5D",\
"$GPGGA,145634.60,4923.90465,N,00907.39883,E,2,12,1.00,336.0,M,47.7,M,,*5D",\
"$PGRMZ,1103,f,3*28",\
"$GPGSA,A,3,,,,,,,,,,,,,1.0,1.0,1.0*33",\
"$PFLAA,0,4105,-5735,1000,1,74073A!DMMHG,0,0,60,10.4,9*49",\
"$PFLAA,0,40260,20167,10636,1,4400E3!EJU18TQ,11,0,231,-0.3,9*7F",\
"$PFLAA,0,-15028,24252,3328,1,3C56EB!GWI6K,215,0,150,0.0,9*09",\
"$PFLAA,0,49736,16153,2647,1,3C66AD!DLH2PM,4,0,191,-7.8,9*4C",\
"$PFLAA,0,28540,21395,11253,1,4400E9!EJU79ZQ,6,0,227,-0.3,9*4E",\
"$PFLAA,0,41057,-5735,3336,1,74073A!RJA126,136,0,181,10.4,9*49",\
"$PFLAA,0,32613,18802,6978,1,3C6593,304,0,230,0.0,0*20",\
"$PFLAA,3,300,200,100,1,74073A!DMRUI,247,0,60,10.4,9*49",\
"$PFLAA,0,3500,,290,1,74073A!DMZUR,247,0,60,10.4,9*49",\
"$PFLAU,18,1,2,1,0,2,0,,,*75",\
"$PFLAA,0,-2000,-1000,300,1,74073A!DMGHH,60,0,60,10.4,9*49",\
"$RPYL,12,-8,0,14,0,1036,0"]

async def tcpsocket():
  sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  server_address = (socket.gethostname(), 2000)
  print('starting up on %s port %s' % server_address, file = sys.stderr)
  sock.bind(server_address)
  sock.listen(1)
  
  while True:
    print('waiting for a connection', file = sys.stderr)
    connection, client_address = sock.accept()
    
    try:
      print('connection from ', client_address, file = sys.stderr)

      index = 0

      while True:   
        try:
          nmeaMessage[4] = "$PFLAA,0,4105,-5735,1000,1,74073A!DMMHG,"+str(index)+",0,60,10.4,9*49"

          if index == 40: del nmeaMessage[14]
          if index == 90: nmeaMessage[13] = "$PFLAU,18,1,2,1,1,,0,,,*75"
          if index == 180: nmeaMessage[13] = "$PFLAU,18,1,2,1,1,,0,,,*75"

          for textList in nmeaMessage:
            connection.sendall(bytes(textList, encoding='utf8')+bytes("\n", encoding='utf8'))
            #print(textList)

          nmeaMessage[13] = "$PFLAU,18,1,2,1,0,,0,,,*75"

          index += 10
          if index == 360:
            index = 0
            nmeaMessage.insert(14, "$PFLAA,0,-2000,-1000,300,1,74073A!DMGHH,60,0,60,10.4,9*49")
          await asyncio.sleep(1)
                
        except socket.error:
          print('connection closed to ', client_address, file = sys.stderr)
          connection.close()
          break
                    
    finally:
      connection.close()

async def main():  
  websocketTask = asyncio.create_task(websocket())
  tcpsocketTask = asyncio.create_task(tcpsocket())
  await tcpsocketTask
  await websocketTask

asyncio.run(main())


