# VocUp - Personal dictionary & Vocabulary builder

## Architecture highlights

- Flow / channel (state & events) if possible
- ~ MVVM (Live Data & Live Event)
- MVI + common contract for games
- ViewModel just for holding dagger component (no custom factories)
- Feature = fragment or fragment set
- Routing via fragment host
- Single activity & back stack management with “flow fragment” (set primary navigation fragment)
- Transitions API ( + custom transitions) with postpone management
- Dark theme

## API keys

Add following keys to your **local.properties**:
- **dictionary.key**=*<your_key>*
- **dictionary.region**=*<your_region>*
- **predictor.key**=*<your_key>*

Otherwise, *ApiStub* will be used

### Microsoft translator

See https://docs.microsoft.com/en-us/azure/cognitive-services/translator/reference/v3-0-reference

### Yandex predictor

See https://yandex.com/dev/predictor/doc/dg/concepts/api-overview-docpage/