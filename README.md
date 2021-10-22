# Undismountable

Undismountable is an addon for WorldGuard that adds a new region flag, `vehicle-dismount`, to control whether players can dismount vehicles in a region.

## Usage

To prevent players from dismounting in a region, simply set the flag `vehicle-dismount` to `deny`:

`/rg flag <region> vehicle-dismount deny`

This will prevent players from dismounting inside this region, the only way to exit a vehicle is if the vehicle is killed or broken.

## Configuration

The default config file looks like this:

```yaml
dismount_message: "§cYou can't dismount here!"
error_timeout: 1000
```

`dismount_message` allows you to send a message to players when they attempt to dismount in a region where it is denied. Set this to `""` to prevent any message from being sent.

`error_timeout` sets the minimum time (in milliseconds) between sending another error message to the same player. Dismount is attempted every tick that the dismount key is held down, so the error messages could get spammy if this is low.
