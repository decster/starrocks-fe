pushd fe
mvn package -Pfull -DskipTests=true | grep -v -E '^(Downloading|Downloaded|Progress|^\[INFO\] Downloading|^\[INFO\] Downloaded)'
popd
exit ${PIPESTATUS[0]}
