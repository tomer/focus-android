# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

# a2po has problems with the folder format of Pontoon and creates resource folders
# like values-es-MX. Android expects values-es-rMX. This script tries to find those
# folders and fixes them.

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd "$parent_path/../../app/src/main/res/"


folder_list=$(find . -maxdepth 1 -type d -iname "values-*-*")
for folder in ${folder_list};
do
	country=$(echo ${folder} | cut -d'-' -f3)
	len=${#country}

	if [ "$len" -eq "2" ]; then
		prefix=$(echo ${folder} | cut -d'-' -f1,2)
	
		fixed_folder="${prefix}-r${country}"

		echo "Fixing ${folder} -> ${fixed_folder}"

		rm -rf "${fixed_folder}"

		mv "$folder" "${fixed_folder}"
	fi
done

