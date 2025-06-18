mvn compile -Dcheckstyle.skip=true -DskipTests=true | grep -v -E '^(Downloading|Downloaded|Progress|^\[INFO\] Downloading|^\[INFO\] Downloaded)'
exit ${PIPESTATUS[0]}
