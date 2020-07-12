#!/bin/bash
#Sample Usage: pushToBintray.sh username apikey owner repo package version publish(0/1) pathToLocalP2Repo

BINTRAY_API=https://api.bintray.com/content
BINTRAY_USER=$1
BINTRAY_API_KEY=$2
BINTRAY_OWNER=$3
BINTRAY_REPO=$4
BINTRAY_PACKAGE=$5
BINTRAY_VERSION=$6
BINTRAY_PUBLISH=$7
PATH_TO_LOCAL_P2_REPO=$8

CURL_USER="-u ${BINTRAY_USER}:${BINTRAY_API_KEY}"
CURL_BASEURL="${BINTRAY_API}/${BINTRAY_OWNER}/${BINTRAY_REPO}/${BINTRAY_PACKAGE}/${BINTRAY_VERSION}"
CURL_HEADER="-H \"X-Bintray-Package:${BINTRAY_PACKAGE}\" -H \"X-Bintray-Version:${BINTRAY_VERSION}\" -H \"X-Bintray-Publish:${BINTRAY_PUBLISH}\""

function main() {
  deploy_updatesite
}

function deploy_updatesite() {
  echo "-------------------------------------------------------------------------------"
  echo "${BINTRAY_USER}"
  echo "${BINTRAY_API_KEY}"
  echo "${BINTRAY_OWNER}"
  echo "${BINTRAY_REPO}"
  echo "${BINTRAY_PACKAGE}"
  echo "${BINTRAY_VERSION}"
  echo "${BINTRAY_PUBLISH}"
  echo "${PATH_TO_LOCAL_P2_REPO}"
  echo "-------------------------------------------------------------------------------"

  if [ ! -z "$PATH_TO_LOCAL_P2_REPO" ]; then
    cd "$PATH_TO_LOCAL_P2_REPO"
    if [ $? -ne 0 ]; then
      echo "$PATH_TO_LOCAL_P2_REPO does not exist"
      exit 1
    fi
  fi

  FILES=./*
  PLUGINDIR=./plugins/*
  FEATUREDIR=./features/*

  for f in $FILES; do
    if [ ! -d $f ]; then
      put_file $f
    fi
  done

  for f in $FEATUREDIR; do
    put_file $f
  done

  for f in $PLUGINDIR; do
    put_file $f
  done

  echo "Done..."
}

function put_file() {
  CURL_CMD="curl -X PUT -T $1 ${CURL_USER} ${CURL_HEADER} ${CURL_BASEURL}/$1"
  echo "$CURL_CMD"
  eval "$CURL_CMD"
  echo ""
  echo "-------------------------------------------------------------------------------"
}

main "$@"
