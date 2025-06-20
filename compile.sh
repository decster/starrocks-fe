pushd fe
mvn compile -Pfull -DskipTests=true | grep -v -E '^(Downloading|Downloaded|Progress|^\[INFO\] Downloading|^\[INFO\] Downloaded)'
popd
exit ${PIPESTATUS[0]}
