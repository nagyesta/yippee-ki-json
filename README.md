```
Y8b    d8P db                                 88    d8P db   88888  .d888b.   .d888b.  88b    88
 Y8b  d8P  YP                                 88   d8P  YP     "8b d8P   Y8b d8P" "Y8b 888b   88
  Y8bd8P                                      88  d8P           88 Y8b.      88     88 8888b  88
   Y88P    88 888b.  888b.   .db.   .db.      88d88K    88      88  "Y88b.   88     88 88Y88b 88
    88     88 88 "8b 88 "8b dP  Yb dP  Yb     88888b    88      88     "Y8b. 88     88 88 Y88b88
    88     88 88  88 88  88 888888 888888 888 88  Y8b   88 888  88       "88 88     88 88  Y8888
    88     88 88 d8P 88 d8P Yb.    Yb.        88   Y8b  88      8P Y8b   d8P Y8b. .d8P 88   Y888
    88     88 888P"  888P"   "Y88   "Y88      88    Y8b 88      88  "Y888P"   "Y888P"  88    Y88
              88     88                                       .d8P                                   
              88     88                                     .d8P"                                    
              88     88                                    88P"                                      
```
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/nagyesta/yippee-ki-json/master/LICENSE)
![JavaCI](https://github.com/nagyesta/yippee-ki-json/workflows/JavaCI/badge.svg?branch=master)
[![codecov](https://codecov.io/gh/nagyesta/yippee-ki-json/branch/master/graph/badge.svg?token=HHSXCEQIDA)](https://codecov.io/gh/nagyesta/yippee-ki-json)

Yippee-Ki-JSON is the perfect library for die-hard CLI users who need to script JSON document manipulations to eliminate
slow and error prone operations.

 ### Planned feature set
 - Filter out certain parts of JSON documents
 - Replace text snippets of matching nodes
    - Supporting conditional replacements
    - In the node values or node names
 - Validate JSON nodes and node values using predefined rules

### Usage
```bash
java -jar yippee-ki-json.jar [--yippee.config=file] --yippee.action=action \
    [--yippee.input=file] [--yippee.allow-overwrite={true|false}] \
    --yippee.output=file

java -jar yippee-ki-json.jar [--yippee.config=file] --yippee.action=action \
    [--yippee.input=file] [--yippee.allow-overwrite={true|false}] \
    --yippee.output-directory=directory

java -jar yippee-ki-json.jar [--yippee.config=file] --yippee.action=action \
    [--yippee.input=directory] [--yippee.includes[0]=pattern] \
    [--yippee.includes[1]=pattern] ... [--yippee.includes[N]=pattern] \
    [--yippee.excludes[0]=pattern] [--yippee.excludes[1]=pattern] ...\
    [--yippee.excludes[N]=pattern] [--yippee.allow-overwrite={true|false}]\
     --yippee.output-directory=directory
```

#### Concept
_Yippee-Ki-JSON_ is a Lightweight JSON manipulation application using [Spring Boot](https://spring.io/projects/spring-boot) and
[JSON Path](https://github.com/json-path/JsonPath) as core. The application uses the following main concepts to carry out the core logic:

1. A configuration YML file
2. Input files
3. An action which is defined in the YML
4. Output files

The configuration YML describes certain named actions which can be done to an input file. Each action has a list of rules which will be
applied in order one after the other to the input file when the action in question is selected to be performed. As the result of each rule,
certain changes can be made to the JSON nodes located by the JSON Path defined in the rule. In the end, the transformed JSON will be
pretty-printed to the output file.

#### Examples
```bash
java -jar yippee-ki-json.jar --yippee.action=filter --yippee.output=./out.json
```
Loads the default configuration file from the working directory, filters the working directory for input files using the default include
pattern and provided that only one input file was found (as output is a single file as well) applies the `filter` action defined in the YML
configuration file to write it to the output.

```bash
java -jar yippee-ki-json.jar --yippee.action=filter --yippee.output-directory=./out
```
Loads the default configuration file from the working directory, filters the working directory for input files using the default include
pattern and applies the `filter` action defined in the YML configuration file to all of the input files. Writes the result into the `./out`
folder using the same file path as the input was relative to the input directory.

```bash
java -jar yippee-ki-json.jar --yippee.action=filter --yippee.output-directory=./out \
    --yippee.allow-overwrite=false
```
Same as example (2) but ensures that no files will be overwritten. If an output file exists we will mark it as failed but won't change the
content.

```bash
java -jar yippee-ki-json.jar --yippee.action=filter --yippee.output-directory=./out \
    --yippee.includes[0]=**/*.json --yippee.excludes[0]=exclude.json
```
Same as example (2) but will include all files using `.json` as extension searching recursively in the input folder except for the single
exclusion: `exclude.json`.

#### Options
##### General options
| Option                      | Description                                                                            |
| --------------------------- | -------------------------------------------------------------------------------------- |
| `--yippee.config`           | The path where the action descriptor can be located. Default: `actions.yml`            |
| `--yippee.action`           | The name of the action we want to execute.                                             |
| `--yippee.input`            | The name of the input file/directory. Default: `./`                                    |
| `--yippee.allow-overwrite`  | Specifies whether we allow overwriting existing outputs. Default: `true`               |
| `--yippee.includes[0..N]`   | Input file include wildcard patterns. Default: `*.json`                                |
| `--yippee.excludes[0..N]`   | Input file exclude wildcard patterns.                                                  |
| `--yippee.output`           | Output file path.                                                                      |
| `--yippee.output-directory` | Output directory path.                                                                 |

##### Spring Boot options
All generic Spring Boot options are supported. Please find a few useful ones below.

| Option                      |                                                      |
| --------------------------- | ---------------------------------------------------- |
| `--logging.level.root`      | Sets the log level of the root package.              |
| `--logging.level.<package>` | Sets the log level of the package named `<package>`. |

###### Log levels

| Log level | Meaning                                                                                                             |
| --------- | ------------------------------------------------------------------------------------------------------------------- |
| `DEBUG`   | Expect seeing detailed information on the parsed configuration. e.g. the list of rules parsed from the config file. |
| `INFO`    | Expect brief messages about the parsing and execution. e.g. the number of rules parsed from the config file.        |
| `WARN`    | Expect only warnings, soft failures.                                                                                |
| `ERROR`   | Lists only errors which are stopping normal execution.                                                              |
