#!/bin/bash

# Copyright 1999-2018 Original Holding Ltd.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
cd `dirname $0`/../target
target_dir=`pwd`

pid=`pgrep -f automated-admin`
if [ -z "$pid" ] ; then
        echo "No Automated admin running."
        exit -1;
fi

echo "The Automated admin(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to Automated admin(${pid}) OK"
