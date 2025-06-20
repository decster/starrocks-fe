pushd fe
mvn compile -DskipTests=true | grep -v -E '^(Downloading|Downloaded|Progress|^\[INFO\] Downloading|^\[INFO\] Downloaded)'
popd
exit ${PIPESTATUS[0]}
