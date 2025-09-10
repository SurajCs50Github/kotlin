Instant-Runoff Voting (Alternative Vote) — Python & Kotlin
This repository contains a reference implementation of Instant-Runoff Voting (IRV), also known as the Alternative Vote, in both Python and Kotlin. The project includes a full suite of unit tests for each implementation.

This project was originally written in Python and was subsequently ported to Kotlin.


How Instant-Runoff Voting Works
The algorithm determines a winner by repeatedly executing the following steps:

Tallies one vote per ballot for the highest-ranked candidate who has not yet been eliminated.

Checks if any candidate has achieved a strict majority (more than 50% of the votes counted in the current round). If so, that candidate is the winner.

If no candidate has a majority, the candidate with the fewest votes is eliminated.

The process repeats until a winner is declared.

Tie-breaking: In the event of a tie for the fewest votes, the candidate who appeared earliest in the initial list of candidates is eliminated.