#
# This script provides completion stuff for rhiot script
#

_rhiot()
{
    local cur=${COMP_WORDS[COMP_CWORD]}
    local prev=${COMP_WORDS[COMP_CWORD-1]}

    case "$prev" in
        rhiot)

            COMPREPLY=( $(compgen -W "--help raspbian-install scan deploy-gateway shell-start" -- $cur) )

        return 0
        ;;

        --help)

        return 0
        ;;
    esac
}

complete -F _rhiot rhiot
