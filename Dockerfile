FROM ruby:2.5

# Next steps:
# - Mount the directory containing artifacts into the container, instead of copying to the image, so that the same image
#   can be used for multiple test runs.

#Install Java JDK
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" >> /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" >> /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --no-tty --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get update && apt-get install -y oracle-java8-installer oracle-java8-set-default

# Install gems needed to run Cucumber tests
WORKDIR /usr/src/app
RUN bundle config --global frozen 1
COPY Gemfile Gemfile.lock ./
RUN bundle install

# Copy JavaSpec artifacts to the image, to be tested
COPY Gemfile Gemfile.lock Rakefile ./
COPY features features/
#COPY console-runner/target/*.jar console-runner/target/
COPY console-runner/target/classes console-runner/target/classes/
