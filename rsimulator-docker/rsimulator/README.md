# Usage #

The latest image is published to Docker Cloud and can be runned by

    docker run -d -t -i -p 8081:8080 --rm --name rsimulator -v $(PWD)/rsimulator_home:/var/rsimulator_home bjuvensjo/rsimulator
