# Welcome to Tetra Backend
This project contains a high quality, fully integrated, production ready system backend, which incorporates many state-of-the-art technologies.

### Backend tech components list

* ZIO and ZIO Test
* ZIO Module Pattern and ZLayer
* Http4s
* Tapir 
* sttp client
* H2Database 
* FlyWay
* Quill 
* PureConfig
* Circe

### Configuration

1. DB Mode <br>
This template allows to use both Mocked Database and Live Database.
For Mock DB we use `zio.Ref`. For Live DB, we use in-memory `H2 Database` with `FlyWay` migrator. 
To specify the DB Mode, update parameter in `application.conf`

2. Updating Database Migrations
`FlyWay` is responsible for initial DB init and migrations. Add your migrations to `main/resources/db/migrate` to change DB behavior

### Thanks to 
* [ZIO Organization](https://zio.dev/)
* [SoftwareMill](https://softwaremill.com/)
* [Adam Fraser](https://github.com/adamgfraser/) and [Adam Warski](https://github.com/adamw) for their excellent tech support
* Many other developers and organizations, who contributed in many different parts of Scala ecosystem 
