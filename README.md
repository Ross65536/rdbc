# rdbc 

a.k.a Ros's Database Connectivity

Simple pet project to study how the Postgres client-server connections work.

Some conclusions:
- Postgres clients connect using TCP with a [specific wire message format](https://www.postgresql.org/docs/current/protocol.html).
- User authentication is done using a relatively little known [SASL-SCRAM protocol](https://datatracker.ietf.org/doc/html/rfc5802).
- SQL injection works if a client is using [Simple Query mode](https://www.postgresql.org/docs/current/protocol-flow.html#PROTOCOL-FLOW-SIMPLE-QUERY): multiple queries can be sent in a single string. This attack is bypassed by leveraging the postgres protocol (and not from any application code). 

> WARNING: Don't use this in production.
