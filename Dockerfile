FROM amazoncorretto:11
LABEL maintainer="Lorenzo Costanzia di Costigliole <lorenzo.costanzia@gmail.com>"
COPY simplelabelcreator /opt/simplelabelcreator
EXPOSE 9000
ENTRYPOINT ["/opt/simplelabelcreator/bin/simplelabelcreator"]