![Yippee-Ki-JSON](.github/assets/yippee-ki-json_small_logo.png)

---

[![GitHub license](https://img.shields.io/github/license/nagyesta/yippee-ki-json?color=blue)](https://raw.githubusercontent.com/nagyesta/yippee-ki-json/main/LICENSE)
[![Java version](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)](https://img.shields.io/badge/Java%20version-17-yellow?logo=java)
[![latest-release](https://img.shields.io/github/v/tag/nagyesta/yippee-ki-json?color=blue&logo=git&label=releases&sort=semver)](https://github.com/nagyesta/yippee-ki-json/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.nagyesta/yippee-ki-json?logo=apache-maven)](https://search.maven.org/artifact/com.github.nagyesta/yippee-ki-json)
[![JavaCI](https://img.shields.io/github/actions/workflow/status/nagyesta/yippee-ki-json/gradle.yml?logo=github&branch=main)](https://github.com/nagyesta/yippee-ki-json/actions/workflows/gradle.yml)
[![badge-abort-mission-armed-green](https://raw.githubusercontent.com/nagyesta/abort-mission/wiki_assets/.github/assets/badge-abort-mission-armed-green.svg)](https://github.com/nagyesta/abort-mission)

[![codecov](https://img.shields.io/codecov/c/github/nagyesta/yippee-ki-json?logo=codecov&token=HHSXCEQIDA)](https://codecov.io/gh/nagyesta/yippee-ki-json)
[![code-climate-maintainability](https://img.shields.io/codeclimate/maintainability/nagyesta/yippee-ki-json?logo=code%20climate)](https://img.shields.io/codeclimate/maintainability/nagyesta/yippee-ki-json?logo=code%20climate)
[![code-climate-tech-debt](https://img.shields.io/codeclimate/tech-debt/nagyesta/yippee-ki-json?logo=code%20climate)](https://img.shields.io/codeclimate/tech-debt/nagyesta/yippee-ki-json?logo=code%20climate)
[![last_commit](https://img.shields.io/github/last-commit/nagyesta/yippee-ki-json?logo=git)](https://img.shields.io/github/last-commit/nagyesta/yippee-ki-json?logo=git)
[![wiki](https://img.shields.io/badge/See-Wiki-informational)](https://github.com/nagyesta/yippee-ki-json/wiki)

--- 

> [!CAUTION]
> Yippee-Ki-JSON End of Support notice: The project will reach end of life and go to public archive after the end of 2024. Please see the announcement [here](https://github.com/nagyesta/yippee-ki-json/discussions/783).

Yippee-Ki-JSON is the perfect library for die-hard CLI users who need to script JSON document manipulations to eliminate
slow and error prone operations.

 ### Primary feature set
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
    [--yippee.relaxed-yml-schema=={true|false}] --yippee.output-directory=directory
    [--yippee.charset=charset]
```

#### Concept
_Yippee-Ki-JSON_ is a Lightweight JSON manipulation application using [Spring Boot](https://spring.io/projects/spring-boot) and
[JSON Path](https://github.com/json-path/JsonPath) as core. The application uses the following main concepts to carry out the core logic:

1. A configuration YML file
2. Input files
3. An action which is defined in the YML
4. Output files

The configuration YML describes certain named actions which can be done to an input file. Each action has a list of rules which will be
applied in the order they were configured one after the other to the input file when the action in question is selected to be performed. 
As the result of each rule, certain changes can be made to the JSON nodes located by the JSON Path defined in the rule. In the end, the 
transformed JSON will be pretty-printed to the output file.

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
| Option                        | Description                                                                          |
| ----------------------------- | ------------------------------------------------------------------------------------ |
| `--yippee.config`             | The path where the action descriptor can be located. Default: `actions.yml`          |
| `--yippee.action`             | The name of the action we want to execute.                                           |
| `--yippee.input`              | The name of the input file/directory. Default: `./`                                  |
| `--yippee.allow-overwrite`    | Specifies whether we allow overwriting existing outputs. Default: `true`             |
| `--yippee.relaxed-yml-schema` | Allows suppression of YML configuration related schema violations. Default: `false`  |
| `--yippee.includes[0..N]`     | Input file include wildcard patterns. Default: `*.json`                              |
| `--yippee.excludes[0..N]`     | Input file exclude wildcard patterns.                                                |
| `--yippee.output`             | Output file path.                                                                    |
| `--yippee.output-directory`   | Output directory path.                                                               |
| `--yippee.charset`            | Default character set used during parsing. Default: `UTF-8`                          |

#### SchemaStore integration
Prefix: `--additional.schema-store.<option>`

| Option              | Description                                                                                           |
| ------------------- | ----------------------------------------------------------------------------------------------------- |
| `catalog-uri`       | The URI of the SchemaStore catalog JSON. Default: `https://www.schemastore.org/api/json/catalog.json` |
| `schema-array-path` | The JSON Path we will use to get the list/array items of the catalog. Default: `$.schemas[*]`         |
| `mapping-name-key`  | The key referencing the name of the schema item. Default: `name`                                      |
| `mapping-url-key`   | The key referencing the URI of the schema item. Default: `url`                                        |

#### HTTP Client
Prefix: `--additional.http.<option>`

| Option                | Description                                                                                     |
| --------------------- | ----------------------------------------------------------------------------------------------- |
| `user-agent`          | The value of the user-agent HTTP header.                                                        |
| `add-default-headers` | Boolean telling the app to add the default HTTP headers automatically. Default: `true`          |
| `min-success-status`  | The minimum (inclusive) HTTP status code value we should consider as successful. Default: `200` |
| `max-success-status`  | The maximum (inclusive) HTTP status code value we should consider as successful. Default: `299` |
| `timeout-seconds`     | The maximum time we want to wait for an HTTP response. Ignored if zero or below. Default: `0`   |

##### Spring Boot options
All generic Spring Boot options are supported. Please find a few useful ones below.

| Option                      | Description                                          |
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
