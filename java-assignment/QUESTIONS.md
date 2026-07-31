# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```
Yes, I would consider some refactoring, mainly to improve consistency. Currently, the Warehouse module follows a hexagonal architecture with a clear separation of ports, use cases and adapters, while the Store and Product modules follow a more traditional layered approach using repositories directly. Having multiple architectural styles within the same codebase increases the learning curve and makes the code less consistent to maintain.

If I were maintaining this application long term, I would standardize on a single architectural approach across all modules. Since the Warehouse module already follows a hexagonal architecture, I would gradually migrate the remaining modules to the same pattern to achieve better consistency, testability and separation of concerns.

I would also replace the in-memory data used for Location and Fulfilment Assignment with persistent database tables. While the current implementation is perfectly suitable for a demo or prototype, storing this data in the database would improve reliability, scalability and consistency with the rest of the application.

```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```
Both approaches have advantages.

The OpenAPI-first approach used for the Warehouse API provides a clear contract before implementation, enables code generation, and keeps documentation synchronized with the API. This is particularly valuable when APIs are consumed by multiple teams or external clients. The trade-off is that it introduces additional tooling and configuration, which may feel unnecessary for very small APIs.

The code-first approach used for Product and Store is quicker to develop and simpler for small CRUD endpoints. However, documentation can become outdated, and there is a greater risk of the implementation diverging from the intended API contract.

My preference would be to adopt the OpenAPI-first approach consistently for externally exposed APIs, as it promotes consistency, maintainability and contract-driven development. For small internal services or prototypes, a code-first approach can still be an appropriate choice.

```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```
Given limited time and resources, I would prioritise testing where it provides the greatest value.

First, I would focus on unit tests for the business logic contained in the use cases and services, since this is where most validation rules and business decisions are implemented. These tests are fast, isolated and provide quick feedback during development.

Next, I would add integration tests for the REST endpoints and persistence layer to verify that the main application flows work correctly from end to end, including request handling, persistence and error scenarios.

Finally, I would include a small number of end-to-end tests covering the application's most critical user journeys. These provide confidence that the overall system behaves correctly without requiring exhaustive end-to-end coverage.

To keep the test suite effective over time, I would require new features and bug fixes to include corresponding tests, review test coverage regularly, and focus on protecting critical business functionality rather than simply aiming for a high coverage percentage. The objective is to build confidence in the application's behaviour while keeping the test suite maintainable and fast.

```