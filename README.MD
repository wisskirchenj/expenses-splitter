# IDEA EDU Course ...

Implemented in the Java Backend Developer Track of hyperskill.org' JetBrain Academy.

Main purpose of doing this project, is to complete the Java Backend Developer track, which now is at 93% applied core topics 
and 98% learned topics overall.

But above all, the project sounds fun and challenging from comments. It is in an early beta version, has few completions,
so it seems like a challenge and opportunity to deepen the Spring boot experiences - though I am working with Spring boot
professionally every day now.

## Technology / External Libraries

- Spring Boot 2.7.1,
- Spring Web MVC - with Tomcat
- H2 Database 2.1.214 or later 
- Spring Data JPA / Hibernate
- Lombok' annotation processors,
- Apache Log4j SLF4J API binding to Log4j 2 logging and
- Junit 5 with
- Mockito (mockito-inline) testing.

## Repository Contents

The sources of main project tasks (6 stages) and the unit, mockito, spring testing.

## Program description

Commands presently supported:
> [date] borrow PersonOne PersonTwo amount -> PersonTwo lent some amount to PersonOne on the specified date

> [date] repay PersonOne PersonTwo amount -> PersonOne repaid an amount to PersonTwo on the date.

> [date] balance [open|close] [(+/- list of names and groups)]-> Calculate and display the list of repayments with names and amounts to be repaid in the natural sorted order for the balance date.
Here open refers to the opening balance on the first day of the month and close refers to the closing balance on that specific date. The list
of names and groups is resolved and the balance filters (i.e. displays only) those owers who are amongst the resolved names

> group create|show|add|remove GROUPNAME [(+/- list of names and groups)] -> create or display groups of persons used in purchase command

> [date] purchase Person itemName amount (+/- list of names and groups) -> a person purchases an item for an amount, which must be spread in the resolved group.
A possible cents remainder of N cents from splitting division is spread between the first N persons in the list ordered by name,
each person pays an extra 0.01.

> secretSanta GROUPNAME -> random gift assignment in a group with rules:
No-one should be assigned to get a gift for themselves in groups larger than 1 person; 
Gift pairs cannot be reciprocal in groups larger than 2 people: in other words, you can't get a gift from a person and give a gift to the same person

> [date] cashback Person itemName amount [(list of [+|-] persons | GROUPS)] -> a commitment to refund some expense to a group, splitting it exactly between
members with the same logic as in purchase.

> [date] writeOff -> clear all transactions (in the database) before and including the given limit date (default today).

> help -> print the list of commands in natural sorted order

> exit

Have fun!

## Project completion

[//]: # (Project was completed on xx.0d.22.)

## Progress

23.06.22 Project started.

26.06.22 Stage 1 completed with Spring Boot CommandLineRunner implementation, Command Pattern used with
CommandLineInterpreter and LineCommand Interface, Mockito Inorder Integration tests

27.06.22 Stage 2 completed, further commands added for groups creating and
splitting, also now transferred from integer transaction amounts to real
money arithmetic on cents basis with rounding etc,

30.06.22 Stage 3 completed, complicated group commands of adding, removing persons and groups added for use in
 purchase commands. Streaming and groupingBy collectors applied.

12.07.22 Stage 4 completed, a lot new functionality: secretSanta, cashBack and writeOff commands added, 
H2 database with @ManyToMany, @Transactional, FetchType.Lazy vs Eager, CascadeType.MERGE etc.. Native @Query for Join select.

17.07.22 Stage 5 completed, balance command gets additional arguments to filter on persons, which have to be
resolved in the same way as done for purchase, cashBack and group add/remove commands
