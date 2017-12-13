# frees-workshop: Building Purely Functional Microservices

In this workshop you will learn how to build from scratch a purely functional application and expose it as a microservice with Freestyle and Freestyle RPC.

This will be a hands on coding session where we will architect a small application based on Algebras and Modules that can be exposed as an RPC microservice supporting Protobuf and Avro serialization protocols.

## Basic Freestyle Structure

We are going to use the [freestyle-seed](https://github.com/frees-io/freestyle-seed.g8) [giter8](https://github.com/foundweekends/giter8) template to create the basic project structure:

```bash
sbt new frees-io/freestyle-seed.g8
```

Result:

```bash
name [Project Name]: frees-rpc-workshop
projectDescription [Project Description]:  Freestyle at Scala eXchange
project [project-name]: functional-microservices
package [freestyle]: scalaexchange
freesVersion [0.4.6]:

Template applied in ./frees-rpc-workshop
```

Run the example:

```bash
cd frees-rpc-workshop
sbt run
```

## Freestyle RPC Setup