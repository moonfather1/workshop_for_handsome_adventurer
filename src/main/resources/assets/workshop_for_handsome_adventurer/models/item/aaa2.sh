fn="$1"
wood1="$2"
woodtypes=('oak' 'jungle' 'birch' 'dark_oak' 'mangrove' 'cherry')
if [[ -e $fn ]];
then
  for wood2 in "${woodtypes[@]}"; do
    fn2="${fn/$wood1/$wood2}"
    if [[ -e $wood2 ]];
    then
      echo "$fn2 exists"
    else
	  #echo copy $fn to $fn2
      cp $fn $fn2
      sed -i "s/$wood1/$wood2/g" $fn2
    fi
  done
fi
echo done.
