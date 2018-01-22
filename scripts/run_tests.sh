#!/bin/bash

cp resources/config/config.edn.test resources/config/config.edn
lein test
