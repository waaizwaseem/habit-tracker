# Habits Data Directory

This directory stores habit information for all users.

## File Format
Each user's habits are stored in a file named `{username}_habits.txt`

Example: `john_doe_habits.txt`

## File Contents
- Habit name
- Description
- Category
- Frequency settings
- Creation date
- Completion status
- Sub-habits (if any)

## Data Structure
Each line represents one habit with pipe-separated values:
`HabitName|Description|Category|FrequencyType|TimesPerDay|IsActive`
