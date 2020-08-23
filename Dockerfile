FROM amazoncorretto:11
LABEL maintainer="Lorenzo Costanzia di Costigliole <lorenzo.costanzia@gmail.com>"
COPY simplelabelcreator /opt/simplelabelcreator
EXPOSE 80
ENTRYPOINT ["/opt/simplelabelcreator/bin/simplelabelcreator", "-Dhttp.port=80"]