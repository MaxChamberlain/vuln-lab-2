## Do Not Allow Exceptions To Expose sensitive information (ERROR01-J)
   Patched by using specified print messages

## Canonicalize Path Names before validating them (FIO16-J)  (Max, Jason)
   Patched by getting canonical name of file before validation

## Normalize Strings before validating them (IDS01-J)  (AJ)
  ### Patched by introducing a NFKC Normalizer


## Sanitize Untrusted Data included in a regular expression (IDS08-J)  (Max, Ronan)
   Patched by using a set regex, and not inserting user text into it


## Use Conservative File Naming conventions (IDS50-J)  (Nathan)
   Vulnerable!!!


## Properly Encode or escape output (IDS51-J)  (Austin)
   Patched by HTML encoding all characters before outputting email to html file


## WHITELISTED_PATH should be changed to whatever path the user is accessing during running for data file (e.g. Email_addresses_20210205.txt)  (Max)
   Patched by requiring cannonical pathname to be in the assets folder of the project directory


## Buffer Overflow / Resource Exhaustion by file size  (Jason)
   Patched by checking file size up to 1MB, after filename sanitization, before BufferedReader initialization


## Does Not Check email address for Script Tags  (Max, Ronan)
   Patched with regex validation


## Does Not Check email address for unescaped "Illegal" Characters  (Max, Ronan)
   Patched with regex validation


## Does Not Check validity of Domain  (Max, Ronan)
   Patched with regex validation


## Does Not Check length of Email Name  (Max, Ronan)
   Patched with regex validation


## Filename is never checked for Validity (null, empty, traversal, etc.)  (Max)
   Patched with regex to check filename validity


## Input Stream is Never Closed if there is an error (memory leak)  (Max)
   Patched by using try-with-resources instead of try-catch-finally
