jar=upload.jar

cd ../bin&&
jar cvf $jar applet/*&&
jarsigner -verbose $jar applet.upload&&
ls -l $jar;
echo done

